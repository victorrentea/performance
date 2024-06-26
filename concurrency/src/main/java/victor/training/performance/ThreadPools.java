package victor.training.performance;

import java.util.concurrent.ExecutorService;

import static victor.training.performance.util.PerformanceUtil.*;

public class ThreadPools {
  public static void main(String[] args) throws InterruptedException {
    // TODO use a fixed number (3) of threads
    ExecutorService executor = null; //Executors. ?

    // TODO create new threads as necessary, and kill idle ones after 1 min
    // ExecutorService executor = Executors.

    // TODO Start 3 threads but max 10 threads, idle threads killed after 1 second
    //  Keep max 5 element in the queue. => rejection possible; experiment with different policies
    // ExecutorService executor = new ThreadPoolExecutor(...)

    for (int i = 0; i < 40; i++) {
      MyTask task = new MyTask(i, 500);
      log("Submitting #" + i);
      executor.submit(task);
      sleepMillis(100);
    }
    // TODO shutdown the executor
  }
  record MyTask(int id, int durationMillis) implements Runnable {
    public void run() {
      log("Start #" + id);
      sleepMillis(durationMillis);
      log("Finish #" + id);
    }
  }
}

