package pers.juumii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import pers.juumii.config.CorsConfig;

@SpringBootApplication
public class MindTraceGatewayApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindTraceGatewayApplication.class, args);
    }
}
