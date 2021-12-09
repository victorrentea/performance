package victor.training.performance.primitives;

// https://dzone.com/articles/shared-variable-optimization-within-a-loop
public class VolatilePuzzle {
	static boolean running = true;  //volatile force re-read from main memory.
	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(() -> {
			System.out.println("Thread starting");
			int n = 0;
			while (running) {
				n++;
			}
			System.out.println("Thread exiting");
		});
		t.start();
		Thread.sleep(5000);



		synchronized (VolatilePuzzle.class) {
			System.out.println("Telling the thread to stop");
			running = false;
			System.out.println("Done");
		}
//		new Thread( () -> {}).start();
	}
}
