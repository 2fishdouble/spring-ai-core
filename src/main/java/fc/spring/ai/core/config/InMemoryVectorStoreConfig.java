package fc.spring.ai.core.config;

import fc.spring.ai.core.embedding.CharNgramEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 内存版 Vector Store 装配：本地 n-gram embedding + SimpleVectorStore。
 * 数据只存在 JVM 内存中，重启后由 RagDocIngestor 重新写入示例文档。
 */
@Configuration
public class InMemoryVectorStoreConfig {

    @Bean
    public CharNgramEmbeddingModel localEmbeddingModel() {
        return new CharNgramEmbeddingModel();
    }

    @Bean
    public SimpleVectorStore vectorStore(CharNgramEmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
