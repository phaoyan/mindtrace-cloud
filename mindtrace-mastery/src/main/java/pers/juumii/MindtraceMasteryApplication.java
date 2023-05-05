package pers.juumii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Hello world!
 *
 */

@SpringBootApplication
@EnableFeignClients
public class MindtraceMasteryApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindtraceMasteryApplication.class,args);
    }
}
