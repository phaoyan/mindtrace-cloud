package pers.juumii;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
@EnableFeignClients
public class MindtraceShareApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindtraceShareApplication.class, args);
    }
}
