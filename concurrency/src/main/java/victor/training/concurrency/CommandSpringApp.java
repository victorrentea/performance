package victor.training.concurrency;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

@Slf4j
public class CommandSpringApp {
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		unRequest();
		log("Gata requestul. Threadul asta se intoarce la joaca in Piscina");
		sleep2(4000);
	}

	private static void unRequest() throws InterruptedException, ExecutionException {
		log("Submitting my order");
		Barman barman = new Barman();

		CompletableFuture<Beer> futureBeer = supplyAsync(barman::pourBeer);
		CompletableFuture<Vodka> futureVodka = supplyAsync(barman::pourVodka);
		log("Waiting for my drinks...");

		Beer beer = futureBeer.get();
		Vodka vodka = futureVodka.get();
		log("Got my order! Thank you lad! " + asList(beer, vodka));
	}
}

class Barman {
	public Beer pourBeer() {
		 log("Pouring Beer...");
		 sleep2(1000);
		 return new Beer();
	 }
	
	 public Vodka pourVodka() {
		 log("Pouring Vodka...");
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
