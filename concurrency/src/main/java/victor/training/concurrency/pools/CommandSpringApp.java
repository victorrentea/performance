package victor.training.concurrency.pools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

@EnableAsync
@SpringBootApplication
public class CommandSpringApp {
	public static void main(String[] args) {
		SpringApplication.run(CommandSpringApp.class, args).close(); // Note: .close to stop executors after CLRunner finishes
	}

	@Bean
	public ThreadPoolTaskExecutor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(1);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("barman-");
		executor.initialize();
		executor.setWaitForTasksToCompleteOnShutdown(true);
		return executor;
	}

}

@Component
class Drinker implements CommandLineRunner {
	@Autowired
	private Barman barman;
	
	
	// TODO [1] inject and use a ThreadPoolTaskExecutor.submit
	// TODO [2] make them return a CompletableFuture + @Async + asyncExecutor bean
	public void run(String... args) throws Exception {
		log("Submitting my order");
		Ale ale = barman.getOneAle();
		Whiskey whiskey = barman.getOneWhiskey();
		log("Got my order! Thank you lad! " + Arrays.asList(ale, whiskey));
	}
}

@Service
class Barman {
	public Ale getOneAle() {
		 log("Pouring Ale...");
		 sleep2(1000);
		 return new Ale();
	 }
	
	 public Whiskey getOneWhiskey() {
		 log("Pouring Whiskey...");
		 sleep2(1000);
		 return new Whiskey();
	 }
}

class Ale {
}

class Whiskey {
}
