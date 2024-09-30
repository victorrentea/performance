package victor.training.performance.jpa;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableCaching
@SpringBootApplication
public class JpaApp {
  public static void main(String[] args) {
    SpringApplication.run(JpaApp.class, args);
  }

  @SneakyThrows
  @GetMapping("external-call/{parentId}")
  public String externalCall(@PathVariable long parentId) {
    Thread.sleep(100);
    return "review for " + parentId;
  }
}
