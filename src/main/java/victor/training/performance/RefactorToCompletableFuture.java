package victor.training.performance;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toSet;

public class RefactorToCompletableFuture {
   public static final int N = 100;

   public static class Worker implements Callable<Set<String>> {
      private final int startVal;
      public Worker(int startVal) {
         this.startVal = startVal;
      }

      @Override
      public Set<String> call() throws Exception {
         Set<String> emailsLocal = new HashSet<>();
         for (int i = startVal; i < startVal + N; i++) {
            String email = EmailFetcher.retrieveEmail(i);
            emailsLocal.add(email);
         }
         return emailsLocal;
      }
   }


   public static void main(String[] args) throws InterruptedException, ExecutionException {
      ExecutorService pool = Executors.newFixedThreadPool(2);

      System.out.println("Started");
      long t0 = System.currentTimeMillis();

      // sparg codul in doua faze: 1) aduna mailuri   2) verifica mailuri
      // ambele faze vor RETURNA nu vor MODIFICA chestii

      // map
      Future<Set<String>> future1 = pool.submit(new Worker(0));
      Future<Set<String>> future2 = pool.submit(new Worker(N));

      // reduce
      Set<String> set1 = future1.get();
      Set<String> set2 = future2.get();

      //nu mai lucrez pe emailurile globale
      Set<String> allEmails = new HashSet<>(); // elimin duplicatele
      allEmails.addAll(set1);
      allEmails.addAll(set2);

      ForkJoinPool myPool = new ForkJoinPool(20);
//paralelizez din nou
      Set<String> emails =
          myPool.submit(() ->

                allEmails.parallelStream()
                  .filter(EmailFetcher::checkEmail)
                  .collect(toSet())

          ).get();
      // regula: nu eziti sa folosesti parallelStream daca ai de facut calcule heavy CPU,
      // cu conditia sa nu ai pe fluxul tau Thread Localuri -> SecurityContextHolder, @Transactional

      // NU folosesti parallelStream d


      long t1 = System.currentTimeMillis();
//      System.out.printf("Result: %,d\n", population.intValue());
      System.out.printf("Emails.size: %,d\n", emails.size());
      System.out.printf("  Emails.size expected: %,d total, %,d unique\n", N*2, N);
      System.out.printf("Emails checks: %,d\n", EmailFetcher.emailChecksCounter.get());
      System.out.println("Time: " + (t1 - t0) + " ms");
      // Note: avoid doing new Thread() -> use thread pools in a real app
   }

}


