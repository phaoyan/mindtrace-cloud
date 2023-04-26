package pers.juumii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Hello world!
 *
 */
@EnableFeignClients
@SpringBootApplication
public class MindTraceEnhancerApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindTraceEnhancerApplication.class, args);
    }
}
