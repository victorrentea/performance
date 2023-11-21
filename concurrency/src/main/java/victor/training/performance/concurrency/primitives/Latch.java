package victor.training.performance.concurrency.primitives;

import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.CountDownLatch;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;
import static victor.training.performance.util.PerformanceUtil.sleepSomeTime;

public class Latch {
	static CountDownLatch startLatch;
	static CountDownLatch finishLatch;
	
	static class Racer extends Thread {
		public void run() {
			positionOnMark();
			waitForGreenLight();
			race();
			reachFinish();
			PerformanceUtil.log("Drive some more");
		}
		
		private void positionOnMark() {
			PerformanceUtil.log("Getting to my position...");
			PerformanceUtil.sleepSomeTime();
		}

		private void waitForGreenLight() {
				PerformanceUtil.log("Wait for green light...");
				// TODO wait for the race start signal
		}
		
		private void race() {
			PerformanceUtil.log("Race...");
			PerformanceUtil.sleepSomeTime(100,700);
		}
		
		private void reachFinish() {
			PerformanceUtil.log("Passing finish line");
			// TODO tell that passed finish
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		new Racer().start();
		new Racer().start();
		new Racer().start();
		PerformanceUtil.log("Wait some time for the racers to get to their places");
		PerformanceUtil.sleepMillis(1000);
		PerformanceUtil.log("Ready... Steady... Go!");
		// TODO trigger start race
		
		PerformanceUtil.log("Wait for all to compute classament");
		// TODO wait for all to finish
		PerformanceUtil.log("All have passed finish line");
		PerformanceUtil.log("Classament: blah blah");
	}
	
}
