package victor.training.concurrency.pools;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

@EnableAsync
@SpringBootApplication
public class PoolApp {
    public static void main(String[] args) {
        SpringApplication.run(PoolApp.class, args)
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

@Component
@Slf4j
class DrinkerService {
    @Autowired
    private Barman barman;

    public List<Object> orderDrinks() throws Exception {
        log.debug("Submitting my order");
        Beer beer = barman.pourBeer().get();
        Vodka vodka = barman.pourVodka();
        log.debug("Got my order! Thank you lad! " + asList(beer, vodka));
        return asList(beer, vodka);
    }

}

@Service
@Slf4j
class Barman {
	@Autowired
	private MyRequestContext requestContext;

	@Async
    public Future<Beer> pourBeer() {
        log.debug("Pouring Beer to "+ requestContext.getCurrentUser()+"...");
        sleep2(1000);
        return completedFuture(new Beer());
    }

    public Vodka pourVodka() {
        log.debug("Pouring Vodka...");
        sleep2(1000);
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
        System.out.println(service.orderDrinks());
    }
}