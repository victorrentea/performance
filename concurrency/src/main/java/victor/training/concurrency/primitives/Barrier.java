package victor.training.concurrency.primitives;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleepSomeTime;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Barrier {
	public static final int NO_DEVELOPERS = 3;
	public static final int NO_DAYS = 5;
	 static CyclicBarrier barrier = new CyclicBarrier(NO_DEVELOPERS);
	// TODO 2: discuss in single thread

	public static void main(String[] args) {
		for (int i = 0; i< NO_DEVELOPERS; i++) {
				new Thread(() -> {
				for (int i1 = 0; i1 < NO_DAYS; i1++) {
					sleepSomeTime(500, 1000);
					log("Waiting for everyone else to start the stand-up");
					try {
						barrier.await();
					} catch (InterruptedException | BrokenBarrierException e) {
//						e.printStackTrace(); // uita-ma. Angajeaza la colt la ASE la patiserie
						throw new RuntimeException(e);
					}
					log("Everyone gathered. Stand UP COMEDY!");
				}
			}).start();
		}
	}
}
