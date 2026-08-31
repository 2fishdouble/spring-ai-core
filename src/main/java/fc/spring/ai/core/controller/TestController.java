package fc.spring.ai.core.controller;

import fc.spring.ai.core.mcp.client.BusinessMcpService;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping
public class TestController {
    private final BusinessMcpService businessMcpService;
    private final ChatClient normalChatClient;
    private final ChatClient jdbcChatClient;
    private final ChatClient jdbcToolChatClient;

    public TestController(
            BusinessMcpService businessMcpService,
            @Qualifier("normalChatClient") ChatClient normalChatClient,
            @Qualifier("jdbcChatClient") ChatClient jdbcChatClient,
            @Qualifier("jdbcToolChatClient") ChatClient jdbcToolChatClient

    ) {
        this.businessMcpService = businessMcpService;
        this.normalChatClient = normalChatClient;
        this.jdbcChatClient = jdbcChatClient;
        this.jdbcToolChatClient = jdbcToolChatClient;
    }

    @GetMapping("/callAddToolDirectly")
    public McpSchema.CallToolResult callAddToolDirectly() {
        return businessMcpService.callAddToolDirectly();
    }

    @GetMapping(value = "/chat/normal/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatNormalFlux(@RequestParam String message) {
        return normalChatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

    @GetMapping(value = "/chat/memory/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatMemoryFlux(@RequestParam String chatId, @RequestParam String message) {
        return jdbcChatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    @GetMapping(value = "/chat/memory/tool/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatMemoryToolFlux(@RequestParam String chatId, @RequestParam String message) {
        return jdbcToolChatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
}
