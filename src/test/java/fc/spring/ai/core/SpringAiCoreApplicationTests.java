package fc.spring.ai.core;

import fc.spring.ai.core.tool.LocalLogisticsService;
import fc.spring.ai.core.tool.LocalOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringAiCoreApplicationTests {
    @Autowired
    private LocalOrderService localOrderService;
    @Autowired
    private LocalLogisticsService localLogisticsService;

    @Test
    void contextLoads() {

    }

}
