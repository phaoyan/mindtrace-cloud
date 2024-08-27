package pers.juumii;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@SpringBootApplication
@NacosPropertySource(dataId = "mindtrace-core.yml", autoRefreshed = true)
@EnableScheduling
public class MindTraceCoreApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindTraceCoreApplication.class, args);
    }
}
