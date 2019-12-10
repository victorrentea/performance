package victor.training.concurrency;

import static victor.training.concurrency.ConcurrencyUtil.log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

public class APlusPlus {
	private static int bumbacTotal;
	private static final Object monitor = new Object();

	public static int culegeBumbac() {
		log("Ma pornesc la cules");
		int bumbac = 0;
		for (int i = 0; i < 1000_000; i++) {
			bumbac++;
		}
		synchronized (monitor) {
			bumbacTotal += bumbac;
		}
		log("AM terminat");
		return 3;
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
		
		Future<Integer> future3 = pool.submit(() -> culegeBumbac());
//		pool.execute(() -> culegeBumbac());
//		pool.execute(() -> culegeBumbac());
		
		ConcurrencyUtil.sleep2(100);
		System.out.println("Am terminat treaba mea");
		
		future3.get(); // blocheaza threadul curent pana e gata si ala
		
		System.out.println("Total = " + bumbacTotal);
		
		long t1 = System.currentTimeMillis();
		System.out.println("Took = " + (t1 - t0));
	}
}
