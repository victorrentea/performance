package victor.training.performance.primitives;

import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleepSomeTime;

import java.util.concurrent.CyclicBarrier;

public class Barrier {
	public static final int NO_PARTICIPANTS = 3;
	public static final int NO_DAYS = 5;
	 static CyclicBarrier barrier = null; // TODO
	// TODO 2: discuss in single thread

	public static void main(String[] args) {
		for (int i=0;i<NO_PARTICIPANTS;i++) {
			new Thread(() -> {
				for (int i1 = 0; i1 < NO_DAYS; i1++) {
					sleepSomeTime(500, 1000);
					log("Waiting for everyone else to start the stand-up");
					// TODO .await at the barrier
					log("Everyone gathered. Stand UP!");
				}
			}).start();
		}
	}
}
