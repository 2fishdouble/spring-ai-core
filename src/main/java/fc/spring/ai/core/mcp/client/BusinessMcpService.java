package fc.spring.ai.core.mcp.client;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BusinessMcpService {

    private final List<McpSyncClient> mcpSyncClients;

    public BusinessMcpService(List<McpSyncClient> mcpSyncClients) {
        this.mcpSyncClients = mcpSyncClients;
    }

    /**
     * 根据配置文件里的 connection 名称获取具体的 Client
     */
    private McpSyncClient getClientByName(String connectionName) {
        return mcpSyncClients.stream()
                .filter(client -> connectionName.equals(client.getClientInfo().title()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到名为 " + connectionName + " 的 MCP Client"));
    }


    public McpSchema.CallToolResult callAddToolDirectly() {
        McpSyncClient client = getClientByName("mcp-server");

        Map<String, Object> arguments = Map.of(
                "a", 15,
                "b", 27
        );

        McpSchema.CallToolRequest request = McpSchema.CallToolRequest
                .builder("calculator_add")
                .arguments(arguments)
                .build();
        return client.callTool(request);
    }
}