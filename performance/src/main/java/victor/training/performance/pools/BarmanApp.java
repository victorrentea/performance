package victor.training.performance.pools;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.performance.ConcurrencyUtil.sleepq;

@EnableAsync
@SpringBootApplication
public class BarmanApp {
    public static void main(String[] args) {
        SpringApplication.run(BarmanApp.class, args)
              ;//  .close(); // Note: .close to stop executors after CLRunner finishes
    }

    // TO discuss propagation of thread local data
    @Bean
    public static CustomScopeConfigurer defineThreadScope() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope("thread", new SimpleThreadScope()); // WARNING: Leaks memory. Prefer 'request' scope or read here: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/SimpleThreadScope.html
        return configurer;
    }

    @Autowired
    private PropagateRequestContext propagateRequestContext;

    @Bean
    public ThreadPoolTaskExecutor executor(@Value("${barman.count}") int size) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(size);
        executor.setMaxPoolSize(size);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("barman-");
        executor.initialize();
        executor.setTaskDecorator(propagateRequestContext);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

}

@Component
@Slf4j
class DrinkerService {
    @Autowired
    private Barman barman;
    @Autowired
    private ThreadPoolTaskExecutor pool;

    public List<Object> orderDrinks() throws ExecutionException, InterruptedException {
        log.debug("Submitting my order");

//        ExecutorService pool = Executors.newFixedThreadPool(2);
//        ExecutorService pool = Executors.newCachedThreadPool();
//        ThreadPoolSECONDS,Executor pool = new ThreadPoolExecutor(
////            2, 2,
////            1, TimeUnit.
//            new ArrayBlockingQueue<>(2),
//            new CallerRunsPolicy());
////            new DiscardOldestPolicy());

        List<Future<Beer>> futureBeers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
//            sleepq(500);
            Future<Beer> futureBeer = pool.submit(() -> barman.pourBeer());
            futureBeers.add(futureBeer);
        }

        Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodka());

        log.debug("aici a plecat cu comanda chelenerul");

        // inteligent
        //
        List<Beer> beers = futureBeers.stream().map(futureBeer -> {
            try {
                return futureBeer.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new IllegalArgumentException();
            }
        }).collect(Collectors.toList());
        Vodka vodka = futureVodka.get();

//        Beer beer = barman.pourBeer();
//        Vodka vodka = barman.pourVodka();
        log.debug("Got my order! Thank you lad! " + asList(beers, vodka));
        return asList(beers, vodka);
    }

}

@Service
@Slf4j
class Barman {
	@Autowired
	private MyRequestContext requestContext;


    public Beer pourBeer() {
        log.debug("Pouring Beer to " + requestContext.getCurrentUser()+"...");
        sleepq(1000);
        return new Beer();
    }

    public Vodka pourVodka() {
        log.debug("Pouring Vodka...");
        sleepq(1000);
        return new Vodka();
    }
}

@Data
class Beer {
}

@Data
class Vodka {
}


// TODO hard-core more DeferredResult

@Slf4j
@RestController
class BarController implements CommandLineRunner {
    @Autowired
    private DrinkerService service;
    @Autowired
	private MyRequestContext requestContext;

    @GetMapping
    public String getDrinks() throws Exception {
        return service.orderDrinks().toString();
    }

    @Override
    public void run(String... args) throws Exception {
		requestContext.setCurrentUser("jdoe");
		requestContext.setRequestId("" + new Random().nextInt(100));
        log.debug(service.orderDrinks().toString());
    }
}