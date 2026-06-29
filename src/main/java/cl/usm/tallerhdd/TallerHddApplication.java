package cl.usm.tallerhdd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class TallerHddApplication {

    public static void main(String[] args) {
        SpringApplication.run(TallerHddApplication.class, args);
    }
}