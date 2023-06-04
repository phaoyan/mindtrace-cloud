package pers.juumii;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@NacosPropertySource(dataId = "mindtrace-path-optimizer.yml", autoRefreshed = true)
public class MindtracePathOptimizerApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindtracePathOptimizerApplication.class, args);
    }
}
