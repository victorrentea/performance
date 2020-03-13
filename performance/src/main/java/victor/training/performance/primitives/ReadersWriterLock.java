package victor.training.performance.primitives;

import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleepSomeTime;

public class ReadersWriterLock {
	
	static String blackboard = "nimic";
	
	static class Student extends Thread {
		public Student() {
			setName("Student" + getName());
		}
		public void run() {
			for (int i = 0; i < 5; i++) {
				sleepSomeTime(50, 200);
				log("Started reading");
				for (int j = 0; j < 3; j++) {
					sleepSomeTime(10, 50);
					log("Reading:" + blackboard);
				}
				log("Finished reading");
				sleepSomeTime(10, 50);
			}
			super.run();
		}
	}
	
	static class Professor extends Thread {
		public Professor() {
			setName("Professor" + getName());
		}
		public void run() {
			for (int i = 0; i < 5; i++) {
				sleepSomeTime(50, 200);
				log("Stared writing");
				sleepSomeTime(10, 50);
				blackboard = "Cuvant" + i;
				for (int j = 0; j < 3; j++) {
					sleepSomeTime(10, 50);
					log("Writing:" + blackboard);
				}
				log("Finished writing");
			}
			super.run();
		}
	}
	
	public static void main(String[] args) {
		new Student().start();
		new Student().start();
		new Student().start();
		new Professor().start();
		new Professor().start();
	}
}

