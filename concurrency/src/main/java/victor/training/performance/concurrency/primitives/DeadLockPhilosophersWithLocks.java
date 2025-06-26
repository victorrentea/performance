package victor.training.performance.concurrency.primitives;

import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class DeadLockPhilosophersWithLocks {
	static class ForkWithLock {
		public final int id;
		public final Lock lock = new ReentrantLock();
		public ForkWithLock(int id) {
			this.id = id;
		}

	}
	
	static class Philosopher extends Thread {
		private final ForkWithLock leftFork;
		private final ForkWithLock rightFork;
		
		public Philosopher(String name, ForkWithLock leftFork, ForkWithLock rightFork) {
			super(name);
			this.leftFork = leftFork;
			this.rightFork = rightFork;
		}

		public void run() {
			ForkWithLock firstFork = leftFork;
			ForkWithLock secondFork = rightFork;

			for (int i=0;i<50;i++) {
				PerformanceUtil.sleepSomeTime();
				PerformanceUtil.log("I'm hungry!");
				
				PerformanceUtil.log("Waiting for first fork (" + firstFork.id + ")");
				firstFork.lock.lock();
				PerformanceUtil.log("Took it");
				PerformanceUtil.sleepSomeTime();
				PerformanceUtil.log("Taking second fork (" + secondFork.id + ")");
				secondFork.lock.lock();

				eat();

				firstFork.lock.unlock();
				PerformanceUtil.sleepSomeTime();
				secondFork.lock.unlock();
				PerformanceUtil.log("Put down forks. Thinking...");
			}
		}

		private void eat() {
			PerformanceUtil.log("Took both forks. Eating...");
			PerformanceUtil.sleepSomeTime();
			PerformanceUtil.log("I had enough. I'm putting down the forks");
		}
	}
	
	public static void main(String[] args) {
		PerformanceUtil.log("Start");
		ForkWithLock[] forks = new ForkWithLock[] {new ForkWithLock(1), new ForkWithLock(2), new ForkWithLock(3), new ForkWithLock(4), new ForkWithLock(5)};
		new Philosopher("Plato", forks[0], forks[1]).start();
		new Philosopher("Konfuzius", forks[1], forks[2]).start();
		new Philosopher("Socrates", forks[2], forks[3]).start();
		new Philosopher("Voltaire", forks[3], forks[4]).start();
		PerformanceUtil.sleepMillis(1000);
		new Philosopher("Descartes", forks[4], forks[0]).start();
	}
}
