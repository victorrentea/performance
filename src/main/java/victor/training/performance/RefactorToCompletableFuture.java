package victor.training.performance;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RefactorToCompletableFuture {
   public static final int N = 100;
   private static final Object monitor = new Object();
   private static final Set<String> emails = new HashSet<>();


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


      // sparg codul in doua faze: 1) aduna mailuri   2) verifica mailuri
      // ambele faze vor RETURNA nu vor MODIFICA chestii

      // wait for the tasks to complete
      future1.get();
      future2.get();

      long t1 = System.currentTimeMillis();
//      System.out.printf("Result: %,d\n", population.intValue());
      System.out.printf("Emails.size: %,d\n", emails.size());
        System.out.printf("  Emails.size expected: %,d total, %,d unique\n", N*2, N);
        System.out.printf("Emails checks: %,d\n", EmailFetcher.emailChecksCounter.get());
      System.out.println("Time: " + (t1 - t0) + " ms");
      // Note: avoid doing new Thread() -> use thread pools in a real app
   }

   public static class Worker1 implements Runnable {
      public void run() {
         for (int i = 0; i < N; i++) {
            String email = EmailFetcher.retrieveEmail(i);

            synchronized (monitor) {
               if (!emails.contains(email)) {
                  if (EmailFetcher.checkEmail(email)) {
                     emails.add(email);
                  }
               }
            }
         }
      }
   }

   public static class Worker2 implements Runnable {
      public void run() {
         for (int i = N; i < N + N; i++) {
            String email = EmailFetcher.retrieveEmail(i);

            synchronized (monitor) {
               if (!emails.contains(email)) {
                  if (EmailFetcher.checkEmail(email)) {
                     emails.add(email);
                  }
               }
            }
         }
      }
   }


}


