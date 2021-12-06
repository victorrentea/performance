package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class RaceBugs {
   public static final int N = 100;
   private static final Object monitor = new Object();
   private static final List<String> emails = new ArrayList<>();


   // TODO Collect all emails with EmailFetcher.retrieveEmail(i)
   // TODO Avoid duplicated emails
   // TODO All email should be checked with EmailFetcher.checkEmail(email)
   // TODO Reduce the no of calls to checkEmail

   public static void main(String[] args) throws InterruptedException, ExecutionException {
      ExecutorService pool = Executors.newFixedThreadPool(2);

      System.out.println("Started");
      long t0 = System.currentTimeMillis();

      Future<?> future1 = pool.submit(new Worker1());
      Future<?> future2 = pool.submit(new Worker2());

      // wait for the tasks to complete
      future1.get();
      future2.get();

      long t1 = System.currentTimeMillis();
//      System.out.printf("Result: %,d\n", population.intValue());
      System.out.printf("Emails.size: %,d\n", emails.size());
//        System.out.printf("  Emails.size expected: %,d total, %,d unique\n", N*2, N);
//        System.out.printf("Emails checks: %,d\n", EmailFetcher.emailChecksCounter.get());
      System.out.println("Time: " + (t1 - t0) + " ms");
      // Note: avoid doing new Thread() -> use thread pools in a real app
   }

   public static class Worker1 implements Runnable {
      public void run() {
         for (int i = 0; i < N; i++) {
            String email = EmailFetcher.retrieveEmail(i);
            synchronized (monitor) {
               emails.add(email);
            }
         }
      }
   }

   public static class Worker2 implements Runnable {
      public void run() {
         for (int i = N; i < N + N; i++) {
            String email = EmailFetcher.retrieveEmail(i);
            synchronized (monitor) {
               emails.add(email);
            }
         }
      }
   }


}


class EmailFetcher {

   private static final List<String> ALL_EMAILS = new ArrayList<>();
   public static AtomicInteger emailChecksCounter = new AtomicInteger(0);

   static {
      List<String> list = IntStream.range(0, RaceBugs.N)
          .mapToObj(i -> "email" + i + "@example.com") // = 50% overlap
          .collect(toList());
//        Collections.shuffle(ALL_EMAILS); // randomize, or
      ALL_EMAILS.addAll(list); // this produces more dramatic results
      ALL_EMAILS.addAll(list);
   }

   /**
    * Pretend some external call
    */
   public static String retrieveEmail(int i) {
      PerformanceUtil.sleepq(10);
      return ALL_EMAILS.get(i);
   }

   public static boolean checkEmail(String email) {
      emailChecksCounter.incrementAndGet();
      PerformanceUtil.sleepSomeTime(0, 1);
      return true;
   }
}