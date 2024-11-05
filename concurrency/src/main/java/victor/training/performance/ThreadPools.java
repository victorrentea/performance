package victor.training.performance;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;
import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

public class ThreadPools {
  public static void main(String[] args) throws InterruptedException {
    // TODO use a fixed number (3) of threads
//    ExecutorService executor = Executors.newFixedThreadPool(3);
    // issue: unbounded queue => long waiting time + OOME

    // TODO create new threads as necessary, and kill idle ones after 1 min
//     ExecutorService executor = Executors.newCachedThreadPool();
     // issue: unbounded thread count => a load spike can kill your app/OS
    // NOT for handling HTTP requests
    // OK for batch processing: when you know the number tasks you will submit

    // TODO Start 3 threads but max 10 threads, idle threads killed after 1 second
    //  Keep max 5 element in the queue. => rejection possible; experiment with different policies
     ExecutorService executor = new ThreadPoolExecutor(
       3, 3,
       1, SECONDS,
       new ArrayBlockingQueue<>(5) // max

//         , new CallerRunsPolicy() // tell the thread that attempted to submit the task to run it
         // a primitive way to "backpressure" = the workers are overrun by their 'master' and tell their master  to 'back off'
         // for a full-depth state of the art backpressure impl see reactive programming (Reactor/RxJava)
         // ⚠️ DANGEROUS because it hurst the upstream thread
         //   in a web app you just paralyzed the HTTP thread > leading to Thread Starvation
     );
    try {
      for (int i = 0; i < 40; i++) {
        MyTask task = new MyTask(i, 500);
        log("Submitting #" + i);
        executor.submit(task);
        sleepMillis(100);
      }
    } finally {
      executor.shutdown(); // remember to do this on any thread pool you manually create
    }
    executor.awaitTermination(10, SECONDS);
    log("All tasks finished");
  }
  record MyTask(int id, int durationMillis) implements Runnable {
    public void run() {
      log("Start #" + id);
      sleepMillis(durationMillis);
      log("Finish #" + id);
    }
  }
}

