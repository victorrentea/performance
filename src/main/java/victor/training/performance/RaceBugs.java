package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


public class RaceBugs {
   public static final int N = 20_000;
   private static final AtomicInteger population = new AtomicInteger(0);
   private static final Object mutex = new Object();
   private static int populationInt = 0;

//    public static  void increase() {
//        synchronized (RaceBugs.class) {
//        populationInt++;
//    }
//    }

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
      System.out.printf("Result: %,d\n", population.intValue());
      System.out.printf("Emails.size: %,d\n", allEmails.size());
      System.out.printf("  Emails.size expected: %,d total, %,d unique\n", N, N / 2);
      System.out.printf("Emails checks: %,d\n", EmailFetcher.emailChecksCounter.get());
      System.out.println("Time: " + (t1 - t0) + " ms");
      // Note: avoid doing new Thread() -> use thread pools in a real app
   }


   private static final Set<String> allEmails = Collections.synchronizedSet(new LinkedHashSet<>());

   // DONE Warmup: fix population++ race
   // DONE Collect all emails with EmailFetcher.retrieveEmail(i)
   // DONE Avoid duplicated emails
   // DONE All email should be checked with EmailFetcher.checkEmail(email)
   // TODO Reduce the no of calls to checkEmail
   public static class Worker1 implements Runnable {
      public void run() {
         for (int i = 0; i < N / 2; i++) {
            String email = EmailFetcher.retrieveEmail(i);

            if (!allEmails.contains(email))
               synchronized (mutex) {
               if (!allEmails.contains(email)) {
                  boolean isValid = EmailFetcher.checkEmailExpen$ive(email);
                  if (isValid) {
                     allEmails.add(email);
                  }
               }
            }
         }
      }
   }
   //26823 with double checked locking pattern
   // 29538 without it

   public static class Worker2 implements Runnable {
      public void run() {
         for (int i = N / 2; i < N; i++) {
            String email = EmailFetcher.retrieveEmail(i); // has 50% chance to return a duplicated email
            if (!allEmails.contains(email))
               synchronized (mutex) {
               if (!allEmails.contains(email)) {
                  boolean isValid = EmailFetcher.checkEmailExpen$ive(email);
                  if (isValid) {
                     allEmails.add(email);
                  }
               }
            }
         }
      }
   }


}


