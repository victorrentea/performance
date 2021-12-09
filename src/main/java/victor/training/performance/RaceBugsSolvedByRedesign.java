package victor.training.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class RaceBugsSolvedByRedesign {
   private static final Logger log = LoggerFactory.getLogger(RaceBugsSolvedByRedesign.class);
   public static final int N = 20_000;
   private static final AtomicInteger population = new AtomicInteger(0);

   public static void main(String[] args) throws InterruptedException, ExecutionException {
      ExecutorService pool = Executors.newFixedThreadPool(2);

      System.out.println("Started");
      long t0 = System.currentTimeMillis();

      Future<Set<String>> future1 = pool.submit(new Worker1());
      Future<Set<String>> future2 = pool.submit(new Worker2());

      // wait for the tasks to complete
      Set<String> part1 = future1.get();
      Set<String> part2 = future2.get();

      Set<String> allEmails = new HashSet<>();
      allEmails.addAll(part1);
      allEmails.addAll(part2);

      // 10K

      // 1) blocking every other dev that wants to use paral stream for CPU (as intended)
      // 2) 12 threads might be too few for the API Icall
      // 3) 12 threads might be too MUCH for the API Icall

      ForkJoinPool checkPool = new ForkJoinPool(4);

      Set<String> checkedEmails =
          checkPool.submit( () ->

         // I am sorry for this syntax
          allEmails.parallelStream()
             .filter(email -> {
                log.debug("filtering " + email);
                return EmailFetcher.checkEmailExpen$ive(email);
             }).collect(Collectors.toSet())

          ).get();


      long t1 = System.currentTimeMillis();
      System.out.printf("Result: %,d\n", population.intValue());
      System.out.printf("Emails.size: %,d\n", checkedEmails.size());
      System.out.printf("  Emails.size expected: %,d total, %,d unique\n", N, N / 2);
      System.out.printf("Emails checks: %,d\n", EmailFetcher.emailChecksCounter.get());
      System.out.println("Time: " + (t1 - t0) + " ms");
      // Note: avoid doing new Thread() -> use thread pools in a real app
   }


   private static final Set<String> allEmails = Collections.synchronizedSet(new HashSet<>());

   // DONE Warmup: fix population++ race
   // DONE Collect all emails with EmailFetcher.retrieveEmail(i)
   // DONE Avoid duplicated emails
   // DONE All email should be checked with EmailFetcher.checkEmail(email)
   // TODO Reduce the no of calls to checkEmail


   public static class Worker1 implements Callable<Set<String>> {
      public Set<String> call() {
         Set<String> myEmails = new HashSet<>();
         for (int i = 0; i < N / 2; i++) {
            myEmails.add(EmailFetcher.retrieveEmail(i));
         }
         return myEmails;
      }
   }
   public static class Worker2 implements Callable<Set<String>> {
      public Set<String> call() {
         Set<String> myEmails = new HashSet<>();
         for (int i = N / 2; i < N; i++) {
            myEmails.add(EmailFetcher.retrieveEmail(i));
         }
         return myEmails;
      }
   }


}


