package victor.training.performance.concurrency.primitives.window;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static victor.training.spring.batch.util.PerformanceUtil.printJfrFile;

public class BoundedQueuePlay {

   public static void main(String[] args) {
      printJfrFile();

      BoundedQueue q = new BoundedQueue(100);

      for (int i = 0; i < 30; i++) {
         new Thread(() -> {
            while (true) {
               q.add("a");
//               sleepNanos(10); // lock contention occurs when no sleeps
            }
         }).start();
      }
      newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
         int size = q.getCopy().size();
         System.out.println("size=" + size);
         if (size != q.getCapacity()) {
            System.exit(2);
         }
      }, 1000, 10, TimeUnit.MILLISECONDS);
   }

}

class BoundedQueue {
   private final int capacity;
   private final Queue<String> queue;

   public BoundedQueue(int capacity) {
      this.capacity = capacity;
      queue = new ArrayDeque<>(capacity);
   }

   public int getCapacity() {
      return capacity;
   }

   public synchronized void add(String element) {
      queue.offer(element);
      while (queue.size() > capacity) {
         queue.poll();
      }
   }

   public synchronized List<String> getCopy() {
      return new ArrayList<>(queue);
   }
}
/**
 * What to optimize? add -> linked list, read -> array list
 */