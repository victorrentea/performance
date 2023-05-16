package victor.training.performance.concurrency.primitives;

import java.util.concurrent.CountDownLatch;

import static victor.training.spring.batch.util.PerformanceUtil.log;
import static victor.training.spring.batch.util.PerformanceUtil.sleepMillis;
import static victor.training.spring.batch.util.PerformanceUtil.sleepSomeTime;

public class Latch {
	static CountDownLatch startLatch;
	static CountDownLatch finishLatch;
	
	static class Racer extends Thread {
		public void run() {
			positionOnMark();
			waitForGreenLight();
			race();
			reachFinish();
			log("Drive some more");
		}
		
		private void positionOnMark() {
			log("Getting to my position...");
			sleepSomeTime();
		}

		private void waitForGreenLight() {
				log("Wait for green light...");
				// TODO wait for the race start signal
		}
		
		private void race() {
			log("Race...");
			sleepSomeTime(100,700);
		}
		
		private void reachFinish() {
			log("Passing finish line");
			// TODO tell that passed finish
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		new Racer().start();
		new Racer().start();
		new Racer().start();
		log("Wait some time for the racers to get to their places");
		sleepMillis(1000);
		log("Ready... Steady... Go!");
		// TODO trigger start race
		
		log("Wait for all to compute classament");
		// TODO wait for all to finish
		log("All have passed finish line");
		log("Classament: blah blah");
	}
	
}
