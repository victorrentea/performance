package victor.training.performance.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class JpaPerformanceApp {
    public static void main(String[] args) {
        SpringApplication.run(JpaPerformanceApp.class, args);
    }

}
