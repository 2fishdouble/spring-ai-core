package fc.spring.ai.core.config;

import fc.spring.ai.core.tool.LocalLogisticsService;
import fc.spring.ai.core.tool.LocalOrderService;
import fc.spring.ai.core.tool.LocalSystemService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.toolsearch.ToolSearchToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.toolsearch.autoconfigure.ToolSearchAdvisorAutoConfiguration;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration;
import org.springframework.ai.model.tool.autoconfigure.ToolCallingAutoConfiguration;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;


@Configuration
public class ChatClientConfig {

    @Bean("normalChatClient")
    public ChatClient normalChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("你是一个专业、严谨且通俗易懂的智能业务助手。")
                .build();
    }

    @Bean
    public JdbcChatMemoryRepository jdbcChatMemoryRepository(JdbcTemplate jdbcTemplate,
                                                             DataSource dataSource,
                                                             PlatformTransactionManager platformTransactionManager
                                                             ) {
        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(JdbcChatMemoryRepositoryDialect.from(dataSource))
                .dataSource(dataSource)
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public ChatMemory jdbcChatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @Bean("jdbcChatClient")
    public ChatClient jbdcChatClient(ChatClient.Builder builder, ChatMemory jdbcChatMemory) {
        return builder
                .defaultSystem("你是一个专业、严谨且通俗易懂的智能业务助手。")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(jdbcChatMemory).build())
                .build();
    }

    /**
     * @see ToolCallingAutoConfiguration
     * @see ToolSearchAdvisorAutoConfiguration
     * @see ChatClientAutoConfiguration
     */
    @Bean("jdbcToolChatClient")
    public ChatClient jbdcToolChatClient(ChatClient.Builder builder,
                                         ChatMemory jdbcChatMemory,
                                         LocalLogisticsService localLogisticsService,
                                         LocalOrderService localOrderService,
                                         LocalSystemService localSystemService
                                         ) {
        return builder
                .defaultSystem("你是一个专业、严谨且通俗易懂的智能业务助手。")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(jdbcChatMemory).build()
                )
                .defaultTools(List.of(
                        localLogisticsService,
                        localOrderService,
                        localSystemService
                ))
                .build();
    }

    /**
     * embedding + vector store 的 RAG ChatClient：
     * 命中向量库的相似文档后交给模型回答，同时叠加 JDBC 聊天记忆以支持多轮追问。
     */
    @Bean("vectorRagChatClient")
    public ChatClient vectorRagChatClient(ChatClient.Builder builder,
                                          VectorStore vectorStore,
                                          ChatMemory jdbcChatMemory) {
        return builder
                .defaultSystem("你是一个专业、严谨且通俗易懂的智能业务助手。回答时请优先依据提供的资料上下文；"
                        + "若资料中找不到相关信息，请如实说明，不要编造。")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(jdbcChatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .similarityThreshold(0.2)
                                        .topK(5)
                                        .build())
                                .build()
                )
                .build();
    }
}