//package victor.training.performance.bar;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import reactor.netty.http.client.HttpClient;
//import reactor.netty.resources.ConnectionProvider;
//import victor.training.performance.drinks.Beer;
//import victor.training.performance.drinks.DillyDilly;
//import victor.training.performance.drinks.Vodka;
//
//@RestController
//@Slf4j
//public class Barman3Reactive {
//  @Autowired
//  private WebClient webClient;
//
//  @GetMapping("/drink/reactive")
//  public Mono<DillyDilly> drink() {
//    Mono<Beer> futureBeer = webClient.get().uri("http://localhost:9999/beer").retrieve().bodyToMono(Beer.class);
//    Mono<Vodka> futureVodka = webClient.get().uri("http://localhost:9999/vodka").retrieve().bodyToMono(Vodka.class);
//    return futureBeer.zipWith(futureVodka, DillyDilly::new);
//  }
//
//
//  @Configuration
//  public static class WebClientConfig {
//    @Bean
//    public WebClient webClient() {
//          return WebClient.create();
//
//      //  🛑 not enough outbound connections ~> increase numbers
//
//      //      ConnectionProvider connectionProvider = ConnectionProvider.builder("myConnectionPool")
////              .maxConnections( 3000)
////              .pendingAcquireMaxCount(3000)
////              .build();
////      ReactorClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(HttpClient.create(connectionProvider));
////      return  WebClient.builder()
////              .clientConnector(clientHttpConnector)
////              .build();
//    }
//  }
//}
//
//
