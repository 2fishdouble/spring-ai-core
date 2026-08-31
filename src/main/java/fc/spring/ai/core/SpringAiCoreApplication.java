package fc.spring.ai.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class SpringAiCoreApplication {
    String desc = """
            用户提问 -> ChatClient (结合 Advisor/Prompt)
                             │
                             ├── 1. 触发 VectorStore RAG 检索 (私有知识库/文档上下文)
                             │
                             ├── 2. 挂载 MCP Tools (来自 List<McpSyncClient> 的远程工具)
                             │
                             └── 3. 提交给 LLM 决策，自动完成工具调用并生成最终回答
            """;

    public static void main(String[] args) {
        SpringApplication.run(SpringAiCoreApplication.class, args);
    }

}
