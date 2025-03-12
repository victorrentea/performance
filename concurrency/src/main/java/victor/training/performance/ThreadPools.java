package victor.training.performance;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MINUTES;
import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

public class ThreadPools {
  public static void main(String[] args) throws InterruptedException {
    // TODO use a fixed number (3) of threads
//    ExecutorService executor = Executors.newFixedThreadPool(3);//null; //Executors. ?
    // 1) out of memory on queue overflow if incoming rate > processing rate for a long time
    //   AND I have no way to BACKPRESSURE the producer:
    //      - watching your work queue
    //.     - k8s not to give you requests because your rediness probe = false
    //      - told be me to stop (RSocket/Reactive Streams)
    //      - Circuit Breaker pausing their request if I keep returning 503/errors
    //      - IMPOSSIBLE FOR realtime event streams that you can't throttle down
    // 2) extra latency
    //.     - when some HUMAN/blocking flow waits for some results
    //      - I don't give a sh*t about background scripts/jobs (by default)
    // 3) server crash = data loss because the work queue is kept in memory
    //      - Fix: store the work until you start processing it in
    //      Kinesis/Kafka/SQS/Rabbit or DB/Redis = INBOX TABLE
    //.     - Twist: if your main queue in-memory is full, push

    // TODO create new threads as necessary, and kill idle ones after 1 min; no queuing
//     ExecutorService executor = Executors.newCachedThreadPool();

    // TODO Start 3 threads but max 10 threads, idle threads killed after 1 second
    //  Keep max 5 element in the queue. => rejection possible; experiment with different policies
     ExecutorService executor = new ThreadPoolExecutor(
         2, // "normal"
         3, // max, on heat
         1, MINUTES,  // keep alive time
         new ArrayBlockingQueue<>(5) // queue size
         , new ThreadPoolExecutor.CallerRunsPolicy() // primitive way to backpressure cross-thread pools
         // Twist: push some extra work to a Kinesis "For-Later-Work-Queue"/"Dead-Letter-Queue" to process off-hours
         // to have a limited size for your in-memory!! queue
         );

    for (int i = 0; i < 40; i++) {
      MyTask task = new MyTask(i, 500);
      log("Submitting #" + i);
      executor.submit(task);
      sleepMillis(100); // a bit of delay between the incoming tasks
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

