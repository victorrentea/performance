package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

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
    }
}
