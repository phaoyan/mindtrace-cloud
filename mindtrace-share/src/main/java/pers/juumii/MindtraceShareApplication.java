package pers.juumii;


import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
@EnableFeignClients
@NacosPropertySource(dataId = "mindtrace-share.yml", autoRefreshed = true)
public class MindtraceShareApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindtraceShareApplication.class, args);
    }
}
