package fc.spring.ai.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 启动时将 classpath:rag-docs/ 下的示例文档分块后写入 Redis Vector Store，
 * 用于跑通 embedding + RAG 链路。chunk 使用固定 id，重复启动会覆盖而非累积。
 */
@Slf4j
@Component
public class RagDocIngestor implements ApplicationRunner {

    private static final String DOC_LOCATION = "classpath*:rag-docs/*";
    private static final String META_SOURCE = "source";

    private static final ResourcePatternResolver RESOURCE_RESOLVER =
            new PathMatchingResourcePatternResolver();

    private final VectorStore vectorStore;
    private final TokenTextSplitter textSplitter = TokenTextSplitter.builder().build();

    public RagDocIngestor(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            int added = ingest();
            log.info("RAG 示例文档写入完成，共写入 {} 个 chunk", added);
        } catch (Exception e) {
            log.warn("RAG 示例文档写入失败(可能 Redis 非 Redis Stack 或不可达): {}", e.getMessage());
        }
    }

    public int ingest() throws Exception {
        List<Document> chunksToAdd = new ArrayList<>();
        Resource[] resources = RESOURCE_RESOLVER.getResources(DOC_LOCATION);
        for (Resource resource : resources) {
            if (!resource.isReadable()) {
                continue;
            }
            String source = resource.getFilename();
            String text = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            assert source != null;
            List<Document> chunks = textSplitter.split(
                    Document.builder().id(source).text(text).metadata(META_SOURCE, source).build());
            int index = 0;
            for (Document chunk : chunks) {
                chunksToAdd.add(Document.builder()
                        .id(source + "-" + index++)
                        .text(chunk.getText())
                        .metadata(META_SOURCE, source)
                        .build());
            }
            log.info("加载文档 {}，切分为 {} 个 chunk", source, chunks.size());
        }
        vectorStore.add(chunksToAdd);
        return chunksToAdd.size();
    }
}
