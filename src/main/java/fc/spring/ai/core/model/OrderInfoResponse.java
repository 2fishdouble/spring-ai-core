package fc.spring.ai.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoResponse {
    private String orderId;
    private String status;
    private BigDecimal totalAmount;
}
