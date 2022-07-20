package victor.training.performance.spring;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

import static victor.training.performance.util.PerformanceUtil.sleepq;

@Component
@Slf4j
public class BarService implements CommandLineRunner {
    public static ThreadLocal<String> currentUserTL = new ThreadLocal<>();
    @Autowired
    private Barman barman;

    @Override
    public void run(String... args) throws Exception { // runs at app startup
        log.debug("Got " + orderDrinks());
    }

    public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException, TimeoutException {
        log.debug("Requesting drinks...");
        long t0 = System.currentTimeMillis();
        currentUserTL.set("jdoe");
        try {
            CompletableFuture<Void> futurePaymentOk = barman.incaseazaBani(100);

            // fix promise-ul din nodeJS
            // Mono.fromSupplier()
            // pe cate threaduri  (pe ce thread pool) se ruleaza pour beer
            CompletableFuture<Beer> beerPromise = futurePaymentOk
//              .thenApply(v -> barman.blocantBeer()) //fara spring magic (@Async)
                    .thenCompose(v -> barman.pourBeer())
                    .exceptionally(e -> new Beer("daialalta"));
            CompletableFuture<Vodka> vodkaPromise = futurePaymentOk.thenCompose(v -> barman.pourVodka());


            CompletableFuture<Beer> beerCompletableFuture =
                    CompletableFuture.supplyAsync(() -> barman.blocantBeer());

            barman.injura("861&$!#&$^!&^&!@%$");

            CompletableFuture<DillyDilly> dillyPromise = beerPromise
                    .thenCombineAsync(vodkaPromise,
                            (b, v) -> new DillyDilly(b, v));

            long t1 = System.currentTimeMillis();
            log.debug("Got my order in {} ms :", t1 - t0);
            return dillyPromise;
        } finally {
            currentUserTL.remove();
        }
    }
}

@lombok.Value
class DillyDilly {
    Beer beer;
    Vodka vodka;
}

@Service
@Slf4j
class Barman {

    @Async("beerPool")
    // max 2 beri in paralel (un COBOL, legacy horror care nu poate fi chemat mai mult de 2 apeluri in paralel)
    public CompletableFuture<Beer> pourBeer() {
        String username = BarService.currentUserTL.get();
        log.debug("Pouring Beer for {}...", username);
        if (true) {
            throw new IllegalArgumentException("NU mai e bere blonda!");
        }
        sleepq(1000); // REST/SOAP/RMI call
        return CompletableFuture.completedFuture(new Beer("blonda"));
    }

    public Beer blocantBeer() {
        log.debug("Pouring Beer...");
        sleepq(1000); // REST/SOAP/RMI call
        return new Beer("blonda");
    }

    @Async("vodkaPool") // max 5 apeluri
    public CompletableFuture<Vodka> pourVodka() {
        log.debug("Pouring Vodka...");
        sleepq(1000); // SQL
        return CompletableFuture.completedFuture(new Vodka());
    }

    @Async
    public void injura(String s) {
        // fire and forget
        if (s != null) {
            System.err.println("CHIAR SE ARUNCA");
            throw new IllegalArgumentException("Iti fac buzunar!");
        }
    }

    @Async("beerPool")
    public CompletableFuture<Void> incaseazaBani(int suma) {
        sleepq(500);
        log.debug("Payment OK");
        if (Math.random() < 0.5) {
            throw new IllegalArgumentException("N-are bani!");
        }
        return CompletableFuture.completedFuture(null);
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
    public CompletableFuture<DillyDilly> getDrinks() throws Exception {
        return service.orderDrinks();
    }
    //</editor-fold>
}

// TODO The Foam Problem: https://www.google.com/search?q=foam+beer+why

@Configuration
class BarConfig {
    //<editor-fold desc="Spring Config">
    //   @Autowired
//   private PropagateThreadScope propagateThreadScope;

    @Bean
    public ThreadPoolTaskExecutor beerPool(@Value("${beer.count}") int barmanCount) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(barmanCount);
        executor.setMaxPoolSize(barmanCount);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("beer-");
        executor.initialize();

executor.setTaskDecorator(new TaskDecorator() {
    @Override
    public Runnable decorate(Runnable runnable) {
        String user = BarService.currentUserTL.get();
        // aici sunt in threadul original
        return () -> {
            BarService.currentUserTL.set(user);
            // aici sunt in worker thread
            try {
                runnable.run();
            } finally {
                BarService.currentUserTL.remove();
            }
        };
    }
});
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor vodkaPool(@Value("${vodka.count}") int barmanCount) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(barmanCount);
        executor.setMaxPoolSize(barmanCount);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("vodka-");
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
    //</editor-fold>
}