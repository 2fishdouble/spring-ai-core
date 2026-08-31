package fc.spring.ai.core.mcp.controller;

import fc.spring.ai.core.mcp.client.BusinessMcpService;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.chat.client.ChatClient;
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

    public TestController(
            BusinessMcpService businessMcpService,
            @Qualifier("normalChatClient") ChatClient normalChatClient) {
        this.businessMcpService = businessMcpService;
        this.normalChatClient = normalChatClient;
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
}
