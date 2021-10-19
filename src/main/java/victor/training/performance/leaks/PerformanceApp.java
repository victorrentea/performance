package victor.training.performance.leaks;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@EnableAsync
@EnableCaching
@EnableFeignClients
@SpringBootApplication
public class PerformanceApp {
    @Bean
    public TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry);
    }


    @Bean
    public ThreadPoolTaskExecutor shepardPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("s-");
//        executor.setRejectedExecutionHandler(new CallerRunsPolicy()); // make the shepard call execute on http pool again
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
    @Bean
    public ThreadPoolTaskExecutor fatPig() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(40);
        executor.setThreadNamePrefix("fat-");
        executor.initialize();
        return executor;
    }

    @EventListener
    public void onStart(ApplicationReadyEvent event) {
        log.info(">>>>>> All Initializations Finised <<<<<<\n==================================================\n");
    }

    public static void main(String[] args) {
        SpringApplication.run(PerformanceApp.class, args);
    }
}
