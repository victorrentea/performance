package victor.training.concurrency;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

public class APlusPlus {
	public static int culegeBumbac() {
		log("Ma pornesc la cules");
		int bumbac = 0;
		for (int i = 0; i < 1000_000; i++) {
			bumbac++;
		}
		ConcurrencyUtil.sleepSomeTime();
		log("AM terminat");
		return bumbac;
	}

	static ExecutorService pool;
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		pool = Executors.newFixedThreadPool(2); // asta face springu
		buzinss();
		pool.shutdown();
	}

	private static void buzinss() throws InterruptedException, ExecutionException {
		long t0 = System.currentTimeMillis();
//		Future<Integer>
		
		List<Future<Integer>> viitoare3uri = new ArrayList<>(); 
		viitoare3uri.add(pool.submit(() -> culegeBumbac()));
		viitoare3uri.add(pool.submit(() -> culegeBumbac()));
		viitoare3uri.add(pool.submit(() -> culegeBumbac()));
		
		sleep2(100);
		log("Am terminat treaba mea");
		
		int bumbacTotal = 0;
		for (Future<Integer> future3 : viitoare3uri) {
			Integer bumbac = future3.get(); // blocheaza threadul curent pana e gata si ala
			bumbacTotal += bumbac;
		}
		
		log("Total = " + bumbacTotal);
		
		long t1 = System.currentTimeMillis();
		log("Took = " + (t1 - t0));
	}
}
