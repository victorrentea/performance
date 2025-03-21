package victor.training.performance.concurrency.primitives;

// https://dzone.com/articles/shared-variable-optimization-within-a-loop
public class Volatile {
	static boolean running = true;
	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(() -> {
			System.out.println("Thread started");
			int n = 0;
			while (running) {
				n++;
//				System.out.println(n); // Heisenbug: log makes bug disappear
//				n/=2;
			}
			System.out.println("Thread exits");
		});
		t.start();
		Thread.sleep(5000);


		System.out.println("Asking the thread to gracefully stop.");
		running = false;
		System.out.println("Waiting for worker to stop...");
		t.join();
		System.out.println("Worker stopped");
	}
}
