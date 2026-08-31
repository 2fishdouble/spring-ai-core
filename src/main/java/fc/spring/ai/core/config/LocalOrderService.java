package fc.spring.ai.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class LocalOrderService {

    @Tool(description = "根据订单号查询系统的订单状态和金额")
    public OrderInfoResponse queryOrder(String orderId) {
        log.info("Query order info: {}", orderId);
        return new OrderInfoResponse(orderId, "已发货", new BigDecimal("100.09"));
    }
}