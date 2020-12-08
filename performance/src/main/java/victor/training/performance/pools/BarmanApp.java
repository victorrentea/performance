package victor.training.performance.pools;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.performance.ConcurrencyUtil.sleepq;

@EnableAsync
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
public class BarmanApp {
    public static void main(String[] args) {
        SpringApplication.run(BarmanApp.class, args)
                .close(); // Note: .close to stop executors after CLRunner finishes
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
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("barman-");
        executor.initialize();
        executor.setTaskDecorator(propagateRequestContext);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

}

@RequiredArgsConstructor
@Component
@Slf4j
class DrinkerService {
    private final Barman barman;

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        new DrinkerService(new Barman()).orderDrinks();
        log.debug("Sent shutdown request");
        log.debug("exit main");
        sleepq(4000);
    }
    public DillyDilly orderDrinks() {
        log.debug("Submitting my order");

        ForkJoinPool icePool = new ForkJoinPool();

        CompletableFuture<Void> drinks = runAsync(() -> pay("drinks"));

        CompletableFuture<Beer> futureBeer = drinks.thenApplyAsync(v -> barman.pourBeer());
        CompletableFuture<Vodka> futureVodka = drinks.thenApplyAsync(v -> barman.pourVodka())
            .thenApplyAsync(vodka -> {
                log.debug("Add ice: heavy CPU Intensive task");
                return vodka;
            }, icePool);
        futureBeer.thenCombine(futureVodka, DillyDilly::new)
            .thenAccept(d -> log.debug("Got my order! Thank you lad! " + d));
        return null;
    }

    public void pay(String comand) {
        //sometime throws
    }
}

@Value
@Slf4j
class DillyDilly {
    Beer beer;
    Vodka vodka;

    public DillyDilly(Beer beer, Vodka vodka) {
        log.debug("Mixing Dilly Dilly");
        sleepq(1000);
        this.beer = beer;
        this.vodka = vodka;
    }
}

@Service
@Slf4j
class Barman {
    public Beer pourBeer() {
        log.debug("Pouring Beer to ");// + requestContext.getCurrentUser()+"...");
        sleepq(1000);
        log.debug("End pouring");
        return new Beer();
    }
    public Vodka pourVodka() {
        log.debug("Pouring Vodka...");
        sleepq(900);
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
    public String getDrinks() throws ExecutionException, InterruptedException, TimeoutException {
        return service.orderDrinks().toString();
    }

    @Override
    public void run(String... args) throws ExecutionException, InterruptedException, TimeoutException {
//		requestContext.setCurrentUser("jdoe");
//		requestContext.setRequestId("" + new Random().nextInt(100));
        log.debug(service.orderDrinks().toString());
    }
}