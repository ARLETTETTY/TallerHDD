package cl.usm.tallerhdd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class TallerHddApplication {

    public static void main(String[] args) {
        SpringApplication.run(TallerHddApplication.class, args);
    }
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
