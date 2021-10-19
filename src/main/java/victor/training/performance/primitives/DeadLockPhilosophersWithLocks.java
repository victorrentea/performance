package victor.training.performance.primitives;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static victor.training.performance.util.PerformanceUtil.*;


public class DeadLockPhilosophersWithLocks {
	static class ForkWithLock {
		public final int id;
		private final Lock lock = new ReentrantLock();
		public ForkWithLock(int id) {
			this.id = id;
		}
		public void take() {
			lock.lock();
		}
		public void putDown() {
			lock.unlock();
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
				sleepSomeTime();
				log("I'm hungry!");
				
				log("Waiting for first fork (" + firstFork.id + ")");
				firstFork.take();
				log("Took it");
				sleepSomeTime();
				log("Taking second fork (" + secondFork.id + ")");
				secondFork.take();
				
				eat();
				
				firstFork.putDown();
				sleepSomeTime();
				secondFork.putDown();
				log("Put down forks. Thinking...");
			}
		}

		private void eat() {
			log("Took both forks. Eating...");
			sleepSomeTime();
			log("I had enough. I'm putting down the forks");
		}
	}
	
	public static void main(String[] args) {
		log("Start");
		ForkWithLock[] forks = new ForkWithLock[] {new ForkWithLock(1), new ForkWithLock(2), new ForkWithLock(3), new ForkWithLock(4), new ForkWithLock(5)};
		new Philosopher("Plato", forks[0], forks[1]).start();
		new Philosopher("Konfuzius", forks[1], forks[2]).start();
		new Philosopher("Socrates", forks[2], forks[3]).start();
		new Philosopher("Voltaire", forks[3], forks[4]).start();
		sleepq(1000);
		new Philosopher("Descartes", forks[4], forks[0]).start();
	}
}
