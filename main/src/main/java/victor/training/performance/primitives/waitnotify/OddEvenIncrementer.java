package victor.training.performance.primitives.waitnotify;

import victor.training.performance.util.PerformanceUtil;

public class OddEvenIncrementer {
	static int n = 0; 
	
	static Object even = new Object();
	static Object odd = new Object();
	
	static class OddIncrementer extends Thread {
		@Override
		public void run() {
			while(n<20) {
				PerformanceUtil.log("Testing " + n);
				synchronized(OddEvenIncrementer.class) {
					synchronized(odd) {
						while (n % 2 == 0) {
							try {
								PerformanceUtil.log("Waiting to become odd");
								odd.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						// n e odd SIGUR
						PerformanceUtil.log("Incrementing " + n);
						n++;
						even.notifyAll(); // a devenit odd
					}
				}
				PerformanceUtil.sleepMillis(1);
			}
		}
	}
	
	static class EvenIncrementer extends Thread {
		@Override
		public void run() {
			while(n<20) {
				PerformanceUtil.log("Testing " + n);
				synchronized(even) {
					synchronized(odd) {
						while (n % 2 == 1) {
							try {
								PerformanceUtil.log("Waiting to become even");
								even.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						// n e even SIGUR
						PerformanceUtil.log("Incrementing " + n);
						n++;
						odd.notifyAll(); // a devenit odd
					}
				}
				PerformanceUtil.sleepMillis(1);
			}
		}
	}
	
	public static void main(String[] args) {
		new OddIncrementer().start();
		new EvenIncrementer().start();
	}

}
