package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@EnableCaching
@EnableMBeanExport
@SpringBootApplication
public class LeaksApp {
    public static void main(String[] args) {
        SpringApplication.run(LeaksApp.class, args);
    }
}
