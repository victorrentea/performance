package victor.training.performance.concurrency.primitives;

import victor.training.performance.util.PerformanceUtil;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepSomeTime;

public class ReadersWriterLock {
	
	static String blackboard = "nimic";
	
	static class Student extends Thread {
		public Student() {
			setName("Student" + getName());
		}
		public void run() {
			for (int i = 0; i < 5; i++) {
				PerformanceUtil.sleepSomeTime(50, 200);
				PerformanceUtil.log("Started reading");
				for (int j = 0; j < 3; j++) {
					PerformanceUtil.sleepSomeTime(10, 50);
					PerformanceUtil.log("Reading:" + blackboard);
				}
				PerformanceUtil.log("Finished reading");
				PerformanceUtil.sleepSomeTime(10, 50);
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
				PerformanceUtil.sleepSomeTime(50, 200);
				PerformanceUtil.log("Stared writing");
				PerformanceUtil.sleepSomeTime(10, 50);
				blackboard = "Cuvant" + i;
				for (int j = 0; j < 3; j++) {
					PerformanceUtil.sleepSomeTime(10, 50);
					PerformanceUtil.log("Writing:" + blackboard);
				}
				PerformanceUtil.log("Finished writing");
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

