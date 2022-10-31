package victor.training.performance.java8.cf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class CompletableFutureApp implements AsyncConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(CompletableFutureApp.class, args);
    }
}
