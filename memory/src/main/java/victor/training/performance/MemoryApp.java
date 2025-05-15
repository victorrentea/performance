package victor.training.performance;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@Slf4j
@EnableAsync
@EnableCaching
@SpringBootApplication
public class MemoryApp {
    public static void main(String[] args) {
        SpringApplication.run(MemoryApp.class, args);
    }

    private static final long t0 = System.currentTimeMillis();

    @EventListener
    public void onStart(ApplicationReadyEvent event) {
        long t1 = System.currentTimeMillis();
        log.info("🌟🌟🌟🌟🌟🌟 MemoryApp Started in {} seconds 🌟🌟🌟🌟🌟🌟", (t1-t0)/1000);
        System.out.println("Open http://localhost:8080");
    }
}
