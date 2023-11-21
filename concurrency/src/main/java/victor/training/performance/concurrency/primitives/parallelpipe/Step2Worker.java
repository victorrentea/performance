package victor.training.performance.concurrency.primitives.parallelpipe;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepSomeTime;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import victor.training.performance.util.PerformanceUtil;


public class Step2Worker implements Runnable {
	private String chunk;
	public Step2Worker(String chunk) {
		this.chunk = chunk;
	}
	public void run() {
		PerformanceUtil.log("Start Processing " + chunk);
		PerformanceUtil.sleepSomeTime(100, 1000);
		PerformanceUtil.log("Done");
	}
	public String toString() {
		return "Step2 for " + chunk; 
	}
}

class Stuff2 {
	public static void main(String[] args) throws InterruptedException {
		ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);
		ThreadPoolExecutor executor;
		RejectedExecutionHandler rejectionPolicy = new RejectedExecutionHandler() {
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				PerformanceUtil.log("I'm here!!!! Ignor: " + r);
				PerformanceUtil.sleepMillis(10);
				executor.execute(r);
			}
		};
		executor = new ThreadPoolExecutor(1, 1, 
				0l, TimeUnit.SECONDS, 
				workQueue,
				rejectionPolicy);
		
		for (int i = 0;i<50;i++) {
			PerformanceUtil.log("Start Parsing");
			PerformanceUtil.sleepSomeTime(10, 100);
			String chunk = "chunk " + i;
			PerformanceUtil.log("Gathered 500:" + chunk);
			executor.execute(new Step2Worker(chunk));
		}
		PerformanceUtil.log("Am terminat de parsat");
		
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.MINUTES);
		PerformanceUtil.log("Am terminat si de updatat. Ma uit la Versiune, lock, autocertificare. ...bla");
		
	}
}
