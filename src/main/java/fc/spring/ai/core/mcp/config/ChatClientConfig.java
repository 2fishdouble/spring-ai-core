package fc.spring.ai.core.mcp.config;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

    @Bean("normalChatClient")
    public ChatClient normalChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("你是一个专业、严谨且通俗易懂的智能业务助手。")
                .build();
    }
}