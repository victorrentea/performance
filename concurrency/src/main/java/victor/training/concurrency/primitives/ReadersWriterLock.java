package victor.training.concurrency.primitives;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleepSomeTime;

public class ReadersWriterLock {

	static ReadWriteLock lock = new ReentrantReadWriteLock();
	static String blackboard = "nimic";
	
	static class Student extends Thread {
		public Student() {
			setName("Student" + getName());
		}
		public void run() {
			for (int i = 0; i < 5; i++) {
				sleepSomeTime(50, 200);


				lock.readLock().lock();
				log("Started reading");
				for (int j = 0; j < 3; j++) {
					sleepSomeTime(10, 50);
					log("Reading:" + blackboard);
				}
				lock.readLock().unlock();

				log("Finished reading");
				sleepSomeTime(10, 50);
			}
			super.run();
		}
	}
	
	static class Profesor extends Thread {
		public Profesor() {
			setName("Professor" + getName());
		}
		public void run() {
			for (int i = 0; i < 5; i++) {
				sleepSomeTime(50, 200);

				lock.writeLock().lock();
				log("Stared writing");
				sleepSomeTime(10, 50);
				blackboard = "Cuvant" + i;
				for (int j = 0; j < 3; j++) {
					sleepSomeTime(10, 50);
					log("Writing:" + blackboard);
				}
				log("Finished writing");
				lock.writeLock().unlock();

			}
			super.run();
		}
	}
	
	public static void main(String[] args) {
		new Student().start();
		new Student().start();
		new Student().start();
		new Profesor().start();
		new Profesor().start();
	}
}

