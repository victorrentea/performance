package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class RaceBugs {
//   private static final List<String> emails = new ArrayList<>();
//   private static final ConcurrentHashMap // niciodata !

   public static final int N = 10_000;

   public static class ThreadA implements Callable<List<String>> {

      @Override
      public List<String> call() throws Exception {
         List<String> emailurileMele = new ArrayList<>();
         for (int i = 0; i < N; i++) {
            String email = EmailFetcher.retrieveEmail(i);
            if (!emailurileMele.contains(email)) {
               emailurileMele.add(email);
            }
         }
         return emailurileMele;
      }
   }

   public static class ThreadB implements Callable<List<String>> {
      @Override
      public List<String> call() throws Exception {
         List<String> emailurileMele = new ArrayList<>();
         for (int  i = N; i < N + N; i++) {
            String email = EmailFetcher.retrieveEmail(i);
            if (!emailurileMele.contains(email)) {
               emailurileMele.add(email);
            }
         }
         return emailurileMele;
      }
   }

   // TODO (bonus): ConcurrencyUtil.useCPU(1)
   // TODO (extra bonus): Analyze with JFR

   public static void main(String[] args) throws InterruptedException, ExecutionException {
      System.out.println("Started");
      long t0 = System.currentTimeMillis();

      ExecutorService pool = Executors.newFixedThreadPool(2);
      Future<List<String>> futureA = pool.submit(new ThreadA());
      Future<List<String>> futureB = pool.submit(new ThreadB());

      List<String> listA = futureA.get();
      List<String> listB = futureB.get();

      List<String> rawEmails = new ArrayList<>(listA); // 10 000
      listB.stream().filter(o -> !rawEmails.contains(o)).forEach(rawEmails::add);


      List<String> chunk1 = rawEmails.subList(0, rawEmails.size() / 2);
      List<String> chunk2 = rawEmails.subList(rawEmails.size() / 2, rawEmails.size());

      Future<List<String>> checkedEmailFuture1 = pool.submit(() -> chunk1.stream().filter(EmailFetcher::checkEmail).collect(toList()));
      Future<List<String>> checkedEmailFuture2 = pool.submit(() -> chunk2.stream().filter(EmailFetcher::checkEmail).collect(toList()));

      List<String> part1 = checkedEmailFuture1.get();
      List<String> part2 = checkedEmailFuture2.get();

      List<String> allEmails = new ArrayList<>(part1);
      allEmails.addAll(part2);





      long t1 = System.currentTimeMillis();
//        System.out.printf("Result: %,d\n", population.longValue());
      System.out.printf("Emails.size: %,d\n", allEmails.size());
//        System.out.printf("  Emails.size expected: %,d total, %,d unique\n", N*2, N);
      System.out.printf("Emails checks: %,d\n", EmailFetcher.emailChecksCounter.get());
      System.out.println("Time: " + (t1 - t0) + " ms");
      // Note: avoid doing new Thread() -> use thread pools in a real app
   }


}


class EmailFetcher {

   private static final List<String> ALL_EMAILS = new ArrayList<>();

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
      return ALL_EMAILS.get(i);
   }

   public static AtomicInteger emailChecksCounter = new AtomicInteger(0);

   public static boolean checkEmail(String email) {
      emailChecksCounter.incrementAndGet();
      PerformanceUtil.sleepSomeTime(2, 3);
      return true;
   }
}