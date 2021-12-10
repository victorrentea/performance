package victor.training.performance.primitives;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static victor.training.performance.util.PerformanceUtil.*;

public class Barrier {
	public static final int NO_DEVELOPERS_IN_A_TEAM = 3;
	public static final int NO_DAYS = 5;
	static CyclicBarrier barrier = new CyclicBarrier(NO_DEVELOPERS_IN_A_TEAM, () -> {
		log("Today's agenda is :  every other thread is now still sleeping" );
		sleepq(1000);
		log("Let's resume!");
	});
	// TODO 2: discuss in single thread

	public static void main(String[] args) {
		for (int i = 0; i < NO_DEVELOPERS_IN_A_TEAM; i++) {
			new Thread(() -> {
				for (int day = 0; day < NO_DAYS; day++) {
					sleepSomeTime(500, 1000);
					log("Waiting for everyone else to start the stand-up");
					try {
						barrier.await();
					} catch (InterruptedException | BrokenBarrierException e) {
						throw new RuntimeException(e);
					}

					log("Everyone gathered. Stand UP!");
				}
			}).start();
		}
	}
}
