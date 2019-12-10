package victor.training.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

public class APlusPlus {
	private static int a;
	private static final Object monitor = new Object();

	public static class ThreadA extends Thread {
		public void run() {
			int aLocal = 0;
			for (int i = 0; i < 1000_000/10; i++) {
				aLocal++;
				aLocal++;
				aLocal++;
				aLocal++;
				aLocal++;
				aLocal++;
				aLocal++;
				aLocal++;
				aLocal++;
				aLocal++;
			}
			synchronized (monitor) {
				a += aLocal;
			}
		}
		
		
	}

	public static class ThreadB extends Thread {
		public void run() {
			int aLocal = 0;
			for (int i = 0; i < 1000_000; i++) {
				aLocal++;
			}
			synchronized (monitor) {
				a += aLocal;
			}
		}
	}

	// TODO (bonus): ConcurrencyUtil.useCPU(1)
	public static void main(String[] args) throws InterruptedException {
		ThreadA threadA = new ThreadA();
		ThreadB threadB = new ThreadB();

		long t0 = System.currentTimeMillis();

		threadA.start();
		threadB.start();
		threadA.join();
		threadB.join();

		long t1 = System.currentTimeMillis();
		System.out.println("Total = " + a);
		System.out.println("Took = " + (t1 - t0));
	}
}
