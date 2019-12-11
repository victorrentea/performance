package victor.training.concurrency.java8;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleepSomeTime;

import java.util.concurrent.CompletableFuture;

import victor.training.concurrency.ConcurrencyUtil;

public class Concurs {

	public static void main(String[] args) {
		
		CompletableFuture<Integer> m1 = supplyAsync(() -> Concurs.m("2"));
		CompletableFuture<Integer> m2 = supplyAsync(() -> Concurs.m("2"));
		
		CompletableFuture.anyOf(m1,m2).thenAccept(o -> {
			log("Primit: " + o);
		});
		
		ConcurrencyUtil.sleep2(3000);
	}
	
	public static Integer m(String s) {
		log("Incep");
		sleepSomeTime(100, 2000);
		log("Gata");
		return Integer.parseInt(s);
	}
}
