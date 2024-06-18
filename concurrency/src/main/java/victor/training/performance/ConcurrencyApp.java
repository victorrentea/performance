package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@Slf4j
@RestController
@SpringBootApplication
public class ConcurrencyApp {
  public static void main(String[] args) {
    SpringApplication.run(ConcurrencyApp.class, args);
  }

  @Bean
  public RestTemplate rest() {
    return new RestTemplate();
  }

}


