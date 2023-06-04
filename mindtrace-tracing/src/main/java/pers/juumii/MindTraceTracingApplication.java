package pers.juumii;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableCaching
@EnableFeignClients
@SpringBootApplication
@NacosPropertySource(dataId = "mindtrace-tracing.yml", autoRefreshed = true)
public class MindTraceTracingApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindTraceTracingApplication.class, args);
    }
}
