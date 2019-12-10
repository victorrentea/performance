package victor.training.concurrency;

import static victor.training.concurrency.ConcurrencyUtil.log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

public class APlusPlus {
	private static int bumbacTotal;
	private static final Object monitor = new Object();

	public static void culegeBumbac() {
		log("Ma pornesc la cules");
		int bumbac = 0;
		for (int i = 0; i < 1000_000; i++) {
			bumbac++;
		}
		synchronized (monitor) {
			bumbacTotal += bumbac;
		}
		log("AM terminat");
	}

	static ExecutorService pool;
	public static void main(String[] args) throws InterruptedException {
		pool = Executors.newFixedThreadPool(2); // asta face springu
		buzinss();
		pool.shutdown();
	}

	private static void buzinss() {
		long t0 = System.currentTimeMillis();
		pool.execute(new Runnable() {
			public void run() {
				culegeBumbac();				
			}
		});
		pool.execute(new Runnable() {
			public void run() {
				culegeBumbac();				
			}
		});
		pool.execute(new Runnable() {
			public void run() {
				culegeBumbac();				
			}
		});
		
		long t1 = System.currentTimeMillis();
		System.out.println("Total = " + bumbacTotal);
		System.out.println("Took = " + (t1 - t0));
	}
}
