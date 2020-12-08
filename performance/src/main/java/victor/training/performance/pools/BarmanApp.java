package victor.training.performance.pools;

import lombok.Data;
import lombok.RequiredArgsConstructor;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static java.util.Arrays.asList;
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
    static ExecutorService pool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        new DrinkerService(new Barman()).orderDrinks();
        pool.shutdown();
        log.debug("Sent shutdown request");
        pool.awaitTermination(2, TimeUnit.SECONDS);
        log.debug("exit main");
    }
    public List<Object> orderDrinks() throws ExecutionException, InterruptedException, TimeoutException {
        log.debug("Submitting my order");

        Future<Beer> futureBeer = pool.submit(barman::pourBeer);
        Future<Vodka> futureVodka = pool.submit(barman::pourVodka);

        log.debug("My requests were submitted");
        futureBeer.cancel(true);
        Beer beer = futureBeer.get();//500, TimeUnit.MILLISECONDS); // how much time main wait here : 1s
        Vodka vodka = futureVodka.get();// how much time main wait here : 0sec:

        log.debug("Got my order! Thank you lad! " + asList(beer, vodka));
        return asList(beer, vodka);
    }

}

@Service
@Slf4j
class Barman {
    public Beer pourBeer() {
        log.debug("Pouring Beer to ");// + requestContext.getCurrentUser()+"...");
        try {
            sleepq(1000);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
        log.debug("End pouring");
//        httpCall()
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