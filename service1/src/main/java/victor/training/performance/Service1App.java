package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.util.concurrent.Executors;


@Slf4j
@RestController
@SpringBootApplication
public class Service1App {
  public static void main(String[] args) {
    SpringApplication.run(Service1App.class, args);
  }

  @Bean
  public RestTemplate rest() {
    return new RestTemplate();
  }

  // increase the connection pool of WebClient over 500 (bottleneck)
//  @Bean
//  public WebClient webClient() {
//    HttpClient httpClient = HttpClient.create(ConnectionProvider
  //    .create("httpClient", 1000));
//    return WebClient.builder()
//            .clientConnector(new ReactorClientHttpConnector(httpClient))
//            .build();
//  }
}


