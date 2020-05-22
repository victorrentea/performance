package victor.training.performance.pools.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.Beer;
import victor.training.performance.pools.Vodka;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
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
        CompletableFuture<Beer> futureBeer = barman.pourBeer();
        CompletableFuture<Vodka> futureVodka = barman.pourVodka();

        Beer beer = futureBeer.get();
        Vodka vodka = futureVodka.get();
        log.debug("Beu: " + beer + " cu " + vodka);
        long t1 = System.currentTimeMillis();
        System.out.println("Took: " + (t1-t0));
    }
}


@Service
@Slf4j
class BarmanSpring {
    @Async("bar")
    public CompletableFuture<Beer> pourBeer() {
        log.debug("Pouring Beer to ...");
        sleep2(1000);
        return completedFuture(new Beer());
    }

    @Async("bar")
    public CompletableFuture<Vodka> pourVodka() {
        log.debug("Pouring Vodka...");
        sleep2(1000);
        return completedFuture(new Vodka());
    }
}