package victor.training.concurrency.pools;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

@EnableAsync
@SpringBootApplication
public class CompletableFuturePlay implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(CompletableFuturePlay.class, args)
				.close(); // Note: .close to stop executors after CLRunner finishes
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

	@Autowired
	private DrinkerService service;

	public void run(String... args) {
		service.orderDrinks();
	}
}

@Component
class DrinkerService {
	@Autowired
	private Barman barman;

	public List<Object> orderDrinks() {
		log("Submitting my order");
		Ale ale = barman.getOneAle();
		Whiskey whiskey = barman.getOneWhiskey();
		log("Got my order! Thank you lad! " + asList(ale, whiskey));
		return asList(ale, whiskey);
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

@Data
class Ale {
}

@Data
class Whiskey {
}



@Slf4j
@RestController
class BarController {
	@Autowired
	private DrinkerService service;
	@GetMapping
	public String getDrinks() {
		return service.orderDrinks().toString();
	}
}