package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
@EnableScheduling
@EnableCaching
@SpringBootApplication
@Slf4j
public class LeaksApp {
    public static void main(String[] args) {
        SpringApplication.run(LeaksApp.class, args);
    }

    @Scheduled(fixedRate = 10)
    public void m() {
        int[] arr = new int[10000];
        log.trace("aaa" + arr.length);
    }
}
