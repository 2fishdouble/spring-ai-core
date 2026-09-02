package fc.spring.ai.core.tool;

import fc.spring.ai.core.model.OrderInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class LocalSystemService {

    @Tool(description = "当前运行操作系统的一些信息")
    public SystemInfo systemInfo() {
        return new SystemInfo("Windows 10", LocalDateTime.now());
    }
}
