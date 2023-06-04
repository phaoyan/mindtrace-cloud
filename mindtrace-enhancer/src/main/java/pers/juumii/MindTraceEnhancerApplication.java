package pers.juumii;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@NacosPropertySource(dataId = "mindtrace-enhancer.yml", autoRefreshed = true)
public class MindTraceEnhancerApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindTraceEnhancerApplication.class, args);
    }
}
