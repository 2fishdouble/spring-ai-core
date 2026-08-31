package fc.spring.ai.core.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
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

    @Bean("jdbcToolChatClient")
    public ChatClient jbdcToolChatClient(ChatClient.Builder builder,
                                         ChatMemory jdbcChatMemory,
                                         LocalOrderService localOrderService) {
        return builder
                .defaultSystem("你是一个专业、严谨且通俗易懂的智能业务助手。")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(jdbcChatMemory).build())
                .defaultTools(List.of(localOrderService))
                .build();
    }
}