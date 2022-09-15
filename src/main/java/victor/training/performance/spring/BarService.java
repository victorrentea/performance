package victor.training.performance.spring;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static victor.training.performance.util.PerformanceUtil.sleepq;

@Component
@Slf4j
public class BarService implements CommandLineRunner {
    @Autowired
    private Barman barman;
    //   private static final ExecutorService threadPool = Executors.newFixedThreadPool(40);
    @Autowired
    private ThreadPoolTaskExecutor pool;

    @Override
    public void run(String... args) throws Exception { // runs at app startup

        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> barman.fetchStateFromOthers("countries"));
        CompletableFuture<Void> f2= CompletableFuture.runAsync(() -> barman.fetchStateFromOthers("fex"));
        CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> barman.fetchStateFromOthers("sites"));

        CompletableFuture<Void> allDone = f1.thenCombineAsync(f2, (v2, v1) -> null)
                .thenCombineAsync(f3, (v1, v2) -> null);

        // non blocking, separate than main, but sequential
        //        CompletableFuture<Void> allDone =
        //                CompletableFuture.runAsync(() -> fetchStateFromOthers("countries"))
        //                        .thenRun(() -> fetchStateFromOthers("sites"));

        allDone
                .exceptionally(e -> {
                    log.info("Liveness = DEAD");// kill me
                    throw new RuntimeException(e);
                })
                .thenRun(() -> log.info("Liveness = LIVE"));

        //      log.debug("Got " + orderDrinks());
    }

    public CompletableFuture<List<Object>> orderDrinks() throws ExecutionException, InterruptedException {
        log.debug("Requesting drinks to {} ...", barman.getClass());
        long t0 = currentTimeMillis();

        // How is @Async working !?

        CompletableFuture<Beer> futureBeer = barman.pourBeer();
        CompletableFuture<Vodka> futureVodka = barman.pourVodka();

        CompletableFuture<List<Object>> futureDrinks = futureBeer
                .thenCombineAsync(futureVodka, (b, v) -> {
                    long t1 = currentTimeMillis();
                    List<Object> drinks = asList(b, v);
                    log.debug("Got my order in {} ms : {}", t1 - t0, drinks);
                    return drinks;
                });

        barman.fireAndForget("asf1957*Q*&%*&");

        // DONE @Async
        // DONE non-blocking HTTP: requirement: your app should support 1000 simultaneous HTTP requests
        // TODO count beers
        // TODO track all unique drinks

        long t1 = currentTimeMillis();
        log.debug("GHTTP thread is FREE here after {} ms : {}", t1 - t0);
        return futureDrinks;
    }
}

@Service
@Slf4j
class Barman {
    //shared mutable data in a WEB API implem = 😱 HORROR!
    private final AtomicInteger beers = new AtomicInteger(0);
    private final Set<Object> drinks = Collections.synchronizedSet(new HashSet<>());

    @Async
    public CompletableFuture<Beer> pourBeer() {
        log.debug("Pouring Beer...");
        beers.incrementAndGet(); // correct
        sleepq(1000); // SOAP call/ REST call
        log.debug("Beer done");
        Beer beer = new Beer("blond");
        drinks.add(beer);
        return CompletableFuture.completedFuture(beer);
    }

    @Async
    public CompletableFuture<Vodka> pourVodka() {
        log.debug("Pouring Vodka...");
        sleepq(1000); // long SQL query
        log.debug("Vodka done");

        Vodka vodka = new Vodka();
        drinks.add(vodka);
        return CompletableFuture.completedFuture(vodka);
    }

    @Retryable(maxAttempts = 3)
    public void fetchStateFromOthers(String refData) {
       log.info("Loading " + refData);
        sleepq(1000);
//        cache put
        if (Math.random() < 0.5) {
            log.error("OUPS " + refData);
            throw new IllegalArgumentException("Oups");
        }

       log.info("Loaded " + refData);
    }

    @Async
    public void fireAndForget(String s) {
        // could take 10 miniutes, the http request staring it left immediately
        throw new RuntimeException("Method not implemented"); // are not visible in the 'requester'
    }
}

@Data
class Beer {
    private final String type;
}

@Data
class Vodka {
    private final String brand = "Absolut";
}

// TODO when called from web, protect the http thread
@Slf4j
@RequestMapping("bar/drink")
@RestController
class BarController {
    //<editor-fold desc="Web">
    @Autowired
    private BarService service;

    @GetMapping
    public CompletableFuture<List<Object>> getDrinks() throws Exception {
        return service.orderDrinks();
    }
    //</editor-fold>
}

// TODO The Foam Problem: https://www.google.com/search?q=foam+beer+why

@EnableRetry
@Configuration
class BarConfig {
    //<editor-fold desc="Spring Config">
    //   @Autowired
    //   private PropagateThreadScope propagateThreadScope;
    //   @Value("${pool.size}")
    //   private int poolSize;
    @Bean
    public ThreadPoolTaskExecutor pool(@Value("${pool.size}") int poolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
//        executor.setQueueCapacity(5); // to little : risk = reject requests (errors)
//        executor.setQueueCapacity(5_000_000); // out of memory + will my client WAIT for 5M items to complete? = 5 minutes
        executor.setQueueCapacity(500); // out of memory + will my client WAIT for 5M items to complete? = 5 minutes
        executor.setThreadNamePrefix("barman-");
        executor.initialize();
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //      executor.setTaskDecorator(propagateThreadScope);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
    //</editor-fold>
}