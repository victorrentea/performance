package victor.training.performance.completableFuture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class CompletableFutureApp implements AsyncConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(CompletableFutureApp.class, args);
    }
}
