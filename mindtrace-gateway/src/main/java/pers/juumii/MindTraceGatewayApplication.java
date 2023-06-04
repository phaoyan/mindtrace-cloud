package pers.juumii;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import pers.juumii.config.CorsConfig;

@SpringBootApplication
@NacosPropertySource(dataId = "mindtrace-gateway.yml", autoRefreshed = true)
public class MindTraceGatewayApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindTraceGatewayApplication.class, args);
    }
}
