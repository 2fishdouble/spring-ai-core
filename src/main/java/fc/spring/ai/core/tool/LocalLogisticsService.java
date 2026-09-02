package fc.spring.ai.core.tool;

import fc.spring.ai.core.model.OrderInfoResponse;
import fc.spring.ai.core.model.OrderLogisticsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @see ToolDefinition
 * @see ToolMetadata
 * @see ToolContext
 * @see ToolCallback
 * @see ToolCallbackProvider
 * @see ToolCallbackResolver
 * @see ToolCallingManager
 * @see ToolCallingAdvisor
 */
@Service
@Slf4j
public class LocalLogisticsService {

    @Tool(description = "根据订单号查询系统的订单物流状态")
    public OrderLogisticsResponse queryLogistics(@ToolParam(
            description = "订单号",
            required = true
    ) String orderId) {
        log.info("Query logistics info: {}", orderId);
        return new OrderLogisticsResponse(orderId, "Transforming...");
    }
}
