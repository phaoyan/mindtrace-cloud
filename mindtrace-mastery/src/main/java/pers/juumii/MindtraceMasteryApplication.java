package pers.juumii;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@NacosPropertySource(dataId = "mindtrace-mastery.yml", autoRefreshed = true)
public class MindtraceMasteryApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindtraceMasteryApplication.class,args);
    }
}
