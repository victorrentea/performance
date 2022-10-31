package victor.training.performance.java8.cf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@EnableScheduling
@SpringBootApplication
public class CompletableFutureApp {
    public static void main(String[] args) {
        SpringApplication.run(CompletableFutureApp.class, args);
    }

}
