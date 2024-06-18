package victor.training.performance;

import java.util.concurrent.ExecutorService;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepSomeTime;

public class ThreadPools {
  public static void main(String[] args) throws InterruptedException {
    // TODO keeps a fixed number (3) of threads
    ExecutorService executor = null; //Executors. ?

    // TODO creates new threads as necessary, and kills idle ones after 1 min
    // ExecutorService executor = Executors.

    // TODO Executor that have at least 3 thread but can grow up to 10 threads,
    //  with a queue of max 5 elements. Kills idle threads after 1 second.
    // ExecutorService executor = new ThreadPoolExecutor(...)

    // TODO Experiment: change queue/pool size to see it grow the pool
    // TODO Experiment: cause a task to be rejected

    for (int i = 0; i < 40; i++) {
      MyTask task = new MyTask(i);
      log("Submitting: " + task);
      executor.submit(task);
      sleepSomeTime(100, 200); // simulate random request rate
    }
    // TODO shutdown the executor
  }
}

record MyTask(int id) implements Runnable {
  public void run() {
    log("Start " + this);
    sleepSomeTime(600, 800);
    log("Finish " + this);
  }
}
