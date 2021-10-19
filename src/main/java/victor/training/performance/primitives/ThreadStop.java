package victor.training.performance.primitives;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepSomeTime;

public class ThreadStop {

	static class MyTask implements Runnable {
		private boolean running = true;
		public void run() {
			try {
				while (running) {
					log("Still alive, waiting...");
					Thread.sleep(1000);
				}
				log("Gracefully stopped execution");
			} catch (InterruptedException e) {
				log("Interrupted. Exiting");
			}
		}
		public void setRunning(boolean running) {
			this.running = running;
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		MyTask myTask = new MyTask();
		
		Thread t = new Thread(myTask);
		t.start();
		
		sleepSomeTime(2000, 3000);
		log("Trying to stop the thread");

		// TODO gracefully stop the thread
		// TODO force the .wait() to interrupt
		
		log("Waiting for thread to finish...");
		t.join();
	}
	
}
