package victor.training.performance.leaks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class LeaksApp {
    public static void main(String[] args) {
        SpringApplication.run(LeaksApp.class, args);
    }
}
