package victor.training.performance.spring;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.SECONDS;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@Slf4j
public class BarService {
    @Autowired
    private Barman barman;
    // niciodata asa: ci cu ThreadPoolTaskExecutor de sprign va rog !
//    ExecutorService threadPool = Executors.newFixedThreadPool(2); // nu aloci un thread pool la fiecare req ci partajezi cu fratii
    @Autowired
    ThreadPoolTaskExecutor threadPool;

    @GetMapping("drink")
    public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
        log.debug("Requesting drinks...");
        long t0 = currentTimeMillis();
        CompletableFuture<Beer> futureBeer = supplyAsync(() -> barman.pourBeer());
        CompletableFuture<Vodka> futureVodka = supplyAsync(() -> barman.pourVodka())
                .thenCompose(v -> barman.addIce(v)) // mai joaca un CF "coase-l si p'asta"
                ;

        // NU ai voie CF.get() ever!!!! defeats the purpose.
//        Beer beer = futureBeer.get(); // threadul tomcatului 1/ 200 sta blocat aici ca 🐂 degeaba
//        Vodka vodka = futureVodka.get();

        CompletableFuture<DillyDilly> futureDilly =
                futureBeer.thenCombine(futureVodka, (beer, vodka) -> new DillyDilly(beer, vodka));

        // promise (JS)  fetch(url).then(result=> {}) ===
        // CompletableFuture (Java) = un Future tunat cu multe metode de chaining de procesare async /
             // iti permite sa inlantui procesari FARA Sa blochezi nici un thread.

        // 💡facem un wait all si apoi get pe fiecare.
        long t1 = currentTimeMillis();
        log.debug("Threadul Tomcatului scapa de req asta in {} ms", t1 - t0);
        return futureDilly;
    }
    // cine scrie catre client rezultatul efectiv pe HTTP response?



    //<editor-fold desc="History Lesson: Async Servlets 10 ani din Servet 3.0">
    @GetMapping("/drink-raw")
    public void underTheHood_asyncServlets(HttpServletRequest request) throws ExecutionException, InterruptedException {
        long t0 = currentTimeMillis();
        AsyncContext asyncContext = request.startAsync(); // I will write the response async

        //var futureDrinks = orderDrinks();
        var futureDrinks = orderDrinks();
        futureDrinks.thenAccept(Unchecked.consumer(dilly -> {
            String json = new ObjectMapper().writeValueAsString(dilly); // serialize as JSON
            asyncContext.getResponse().getWriter().write(json);// the connection was kept open
            asyncContext.complete(); // close the connection to the client
        }));
        log.info("Tomcat's thread is free in {} ms", currentTimeMillis() - t0);
    }
    //</editor-fold>

    //<editor-fold desc="Starve ForkJoinPool">
    @GetMapping("starve")
    public String starveForkJoinPool() {
        int tasks = 10 * Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < tasks; i++) {
            CompletableFuture.runAsync(() -> sleepMillis(1000));
        }
        // OR
        // List<Integer> list = IntStream.range(0, tasks).boxed().parallel()
        //       .map(i -> {sleepq(1000);return i;}).collect(toList());
        return "ForkJoinPool.commonPool blocked for 10 seconds";
    }
    //</editor-fold>
}

@Slf4j
@lombok.Value
class DillyDilly {
     Beer beer;
     Vodka vodka;

    public DillyDilly(Beer beer, Vodka vodka) {
        this.beer = beer;
        this.vodka = vodka;
        log.info("Amestec cocktail"); // unde ruleaza lambda de combina rezultatele celor 2 bauturi?
    }
}

@Service
@Slf4j
class Barman {

    public Beer pourBeer() {
        log.debug("Pouring Beer...");
        sleepMillis(1000); // imagine slow REST call
        log.debug("Beer done");
        return new Beer("blond");
    }

    public Vodka pourVodka() {
        log.debug("Pouring Vodka...");
        sleepMillis(1000); // long query maria DB conn ai uitat sa pui
        // indecsii in PROD!!!. ai pus indecsi pe toti si dupa faci
        // un un INSEEEEEEEEEEEEEERT
        log.debug("Vodka done");
        return new Vodka();
    }

//    public Vodka addIce(Vodka vodka) { // in0memory instantaneous tranformation
    public CompletableFuture<Vodka> addIce(Vodka vodka) { // NETWORK call (alt api call)
        return supplyAsync(() -> {
            vodka.setIce(true);
            return vodka;
        }, CompletableFuture.delayedExecutor(1, SECONDS));
    }
}

@Data
class Beer {
    private final String type;
}

@Data
class Vodka {
    private final String brand = "Absolut";
    private boolean ice; // PROST PROST PROST DATE MUTABILE IN MULTITHREAD. Kididing !! NICIODATA  Ca da in race conditions.
}

@Configuration
class BarConfig {
    //<editor-fold desc="Custom thread pool">
    @Bean // defineste un bean spring numit "barPool" de tip ThreadPoolTaskExecutor
    public ThreadPoolTaskExecutor barPool(MeterRegistry meterRegistry, @Value("${bar.pool.size}") int n) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(n);
        executor.setMaxPoolSize(n);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("barman-");
//        executor.setTaskDecorator(new MonitorQueueWaitingTime(meterRegistry.timer("barman-queue-time")));
        executor.initialize();
        return executor;
    }
    //</editor-fold>
}