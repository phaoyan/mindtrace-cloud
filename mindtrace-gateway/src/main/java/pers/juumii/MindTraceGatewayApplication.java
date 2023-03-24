package pers.juumii;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */

@SpringBootApplication
public class MindTraceGatewayApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindTraceGatewayApplication.class, args);
        System.out.println("SA CONFIGS: \n" + SaManager.getConfig());
    }
}
