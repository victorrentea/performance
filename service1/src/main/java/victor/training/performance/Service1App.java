package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;


@Slf4j
@RestController
@EnableAsync
@SpringBootApplication
public class Service1App {
  public static void main(String[] args) {
    SpringApplication.run(Service1App.class, args);
  }

  @Bean
  public RestTemplate rest() {
    return new RestTemplate();
  }
//  @Bean
//  public WebClient webClient() {
//    return WebClient.create();
//  }

  @Bean
  public WebClient webClient() {
    HttpClient httpClient = HttpClient.create(ConnectionProvider
            .create("httpClient", 1000));
    return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
  }


  //  @Bean
//  public ThreadPoolTaskExecutor barPool(@Value("${bar.pool.size}") int barPoolSize) {
//    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//    executor.setCorePoolSize(barPoolSize);
//    executor.setMaxPoolSize(barPoolSize); // equal to avoid pushing too hard on remote systems in times of trouble
//
//    executor.setQueueCapacity(200);
//    // factors:
//    // - # of workers
//    // - avg duration of a task => how much are you willing to wait
//    // - memory available for the queue
//
//    executor.setThreadNamePrefix("bar-");
//    executor.initialize();
//    return executor;
//  }
}


