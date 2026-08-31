package fc.spring.ai.core.mcp.controller;

import fc.spring.ai.core.mcp.client.BusinessMcpService;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TestController {
    private final BusinessMcpService businessMcpService;
    @GetMapping("/callAddToolDirectly")
    public McpSchema.CallToolResult callAddToolDirectly() {
        return businessMcpService.callAddToolDirectly();
    }
}
