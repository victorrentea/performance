package victor.training.performance.leaks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class LeaksApp {
//    @Bean
//    ThreadPoolTaskExecutor unExecutorExplicit() {
//        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
//        threadPoolTaskExecutor.setCorePoolSize(100);
//
//        return threadPoolTaskExecutor;
//    }

    public static void main(String[] args) {
        SpringApplication.run(LeaksApp.class, args);
    }
}
