package fc.spring.ai.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class SpringAiCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiCoreApplication.class, args);
    }

}
