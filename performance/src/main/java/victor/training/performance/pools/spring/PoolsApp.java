package victor.training.performance.pools.spring;

import ch.qos.logback.core.BasicStatusManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.experimental.theories.DataPoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.BarmanJavaSE;
import victor.training.performance.pools.Beer;
import victor.training.performance.pools.Vodka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.performance.ConcurrencyUtil.sleep2;

@Slf4j
@EnableAsync
@SpringBootApplication
public class PoolsApp implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(PoolsApp.class);
    }

    @Bean
    public ThreadPoolTaskExecutor bar() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("bar-");
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
    @Autowired
    private BarmanSpring barman;
    @Override
    public void run(String... args) throws Exception {
        long t0 = System.currentTimeMillis();
        // Java SE, dar atunci se foloseste common pool-ul din JVM. Ala unu la parinti
//        CompletableFuture<Beer> futureBeer = supplyAsync(() -> new BarmanJavaSE().pourBeer());
//        CompletableFuture<Vodka> futureVodka = supplyAsync(() -> new BarmanJavaSE().pourVodka());
        // Spring @Async ai control asupra poolului
        CompletableFuture<Beer> futureBeer = barman.pourBeer();
        CompletableFuture<Vodka> futureVodka = barman.pourVodka();


        System.out.println("main face chestii...");
        sleep2(100);
        futureBeer.cancel(true);
        futureVodka.cancel(true);

        CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombine(futureVodka,
                (beer, vodka) -> new DillyDilly(beer, vodka));

//        Beer beer = futureBeer.get();
//        Vodka vodka = futureVodka.get();
//        DillyDilly dilly = futureDilly.get();

        futureDilly.thenAccept(dilly ->
            log.debug("Beu: " + dilly)
                );
        log.debug("Plec acasa...");
        long t1 = System.currentTimeMillis();
        System.out.println("Took: " + (t1-t0));

//        synchronized () {
//            listaComuna.add("aa");
////            log.debug() nu breakpoint
//            sleep(random(1ms .. 50ms))
//        }
//        synchronized () {
//            listaComuna.get("aa");
//        }
    }
//    List<String> listaComuna;
}

@Data
@Slf4j
class DillyDilly {
    private final Beer beer;
    private final Vodka vodka;

    public DillyDilly(Beer beer, Vodka vodka) {
        log.debug("Fac cocktail");
        this.beer = beer;
        this.vodka = vodka;
    }
}


@Service
@Slf4j
class BarmanSpring {
    @Async("bar")
    public CompletableFuture<Beer> pourBeer() {
        log.debug("Pouring Beer to ...");
        sleep2(2000);
        return completedFuture(new Beer());
    }

    @Async("bar")
    public CompletableFuture<Vodka> pourVodka() {
        log.debug("Pouring Vodka...");
        sleep2(1000);
        return completedFuture(new Vodka());
    }
}