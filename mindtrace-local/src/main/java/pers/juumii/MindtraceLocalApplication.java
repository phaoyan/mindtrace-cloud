package pers.juumii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class MindtraceLocalApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindtraceLocalApplication.class, args);
    }
}
