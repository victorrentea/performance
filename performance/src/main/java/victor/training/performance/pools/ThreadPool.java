package victor.training.performance.pools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleepSomeTime;

public class ThreadPool {
	
	static AtomicInteger integer = new AtomicInteger(0);
	
	static class MyTask implements Runnable {
		public int id = integer.incrementAndGet();
		public void run() {
			log("Start work item #"+id);
			sleepSomeTime(600, 800);
			log("Finish work item #"+id);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		// TODO Executor that keeps a fixed number (3) of threads until it is shut down
		 ExecutorService executor = null; //Executors. ? 
		
		// TODO Executor that grows the thread pool as necessary, and kills inactive ones after 1 min
		// TODO ExecutorService executor = Executors. ?
		
		// TODO Executor that have at least 3 thread but can grow up to 10 threads. Inactive threads die in 1 second.
		// TODO Vary the fixed-sized queue to see it grow the pool and then Rejecting tasks 
		
		for (int i =0;i<40;i++) {
			MyTask task = new MyTask();
			log("Submitted new task #" + task.id);
			executor.execute(task);
			sleepSomeTime(100, 200);
//			queue.offer(new MyTask(), 1, TimeUnit.HOURS);
		}
		// TODO shutdown the executor !
	}
}

