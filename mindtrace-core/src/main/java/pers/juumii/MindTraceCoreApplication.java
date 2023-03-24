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
public class MindTraceCoreApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MindTraceCoreApplication.class, args);
    }
}
