package victor.training.performance.spring;

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

@Slf4j
@EnableAsync
@EnableCaching
@EnableFeignClients
@SpringBootApplication
public class PerformanceApp {
    private static final long t0 = System.currentTimeMillis();

    // enables the use of @Timed
    @Bean
    public TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry);
    }

    @EventListener
    public void onStart(ApplicationReadyEvent event) {
        long t1 = System.currentTimeMillis();

        log.info(">>>>>> All Initializations Finished in {} seconds <<<<<<\n==================================================\n", (t1-t0)/1000);
    }

    public static void main(String[] args) {
        SpringApplication.run(PerformanceApp.class, args);
    }
}
