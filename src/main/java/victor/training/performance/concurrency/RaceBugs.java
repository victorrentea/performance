package victor.training.performance.concurrency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;


@Slf4j
@RequiredArgsConstructor
public class RaceBugs {
   private final ExternalDependency externalSystem;

   // TODO Collect all emails with dependency#retrieveEmail(id) - takes time (networking)
   // TODO Eliminate duplicated emails (case insensitive)
   // TODO Only allow emails for which true == dependency#isEmailValid(email) - takes time (networking)
   // TODO Avoid calling checkEmail twice for the same email

   private final List<String> allEmails = new ArrayList<>();


//   private static int j = 0;
   public static void main(String[] args) throws Exception {

//      for (int i = 0; i < 10_000; i++) {
//         j++;
//      }



      ExternalDependencyFake dependency = new ExternalDependencyFake(20_000);
      List<Integer> ids = IntStream.range(0, 20_000).boxed().collect(toList());
      Collection<String> results = new RaceBugs(dependency).retrieveEmailsInParallel(ids);

      log.debug("No of emails returned: " + results.size());
      // log.debug("No of emails checked: " + dependency.emailChecksPerformed());
   }

   //region SameCodeAsInIntro
   public Collection<String> retrieveEmailsInParallel(List<Integer> ids) throws Exception {
      // split the work in two
      List<Integer> firstHalf = ids.subList(0, ids.size() / 2);
      List<Integer> secondHalf = ids.subList(ids.size() / 2, ids.size());

      CompletableFuture<List<String>> firstHalfFuture = supplyAsync(() -> resolve(firstHalf));
      CompletableFuture<List<String>> secondHalfFuture = supplyAsync(() -> resolve(secondHalf));

      List<String> toate = firstHalfFuture.thenCombine(secondHalfFuture, this::concat).get();
      List<String> distinctEmails = removeDuplicatesInsensitive(toate);


      Stream<String> stream = distinctEmails.parallelStream()
          // elementele din stream vor fi procesate in paralel pe mai multe threaduri
          // -- PE CATE? = N(CPU) - 1 < main pune si el mana
          // -- DIN CE THREAD POOL: ForkJoinPool.commonPool < care este un thread pool GLOBAL per JVM
          .filter(externalSystem::isEmailValid)  // PERICULOS intr-o app mare: nu stii cine mai ruleaza cu tine pe thread pool > Thread stravation
          // IN GENERAL, parallelStream trebuie folosit exclusiv pentru CHESTII care FAC DOAR CPU (NU asteapta dupa exterior)
          .peek(e -> {
//             PerformanceUtil.sleepq(100);
             log.info("Am validat emailul " + e);
          });

      ForkJoinPool pool = new ForkJoinPool(200); // aici ruleaza parallelStreamul de fapt

      // asa poti lansa prallel Streamuri pe thread pooluri private:
      // terminand streamul (.collect) intr-un task submis intr-un ForkJoinPool de-al tau
      // FACI ASTA DOAR DACA VREI SA FACI I/O (REST,DB)
      List<String> result = pool.submit( ()-> stream.collect(toList())  ).get();


      // la fiecare 30 min un scheduler vrea sa trimita mailuri in parallel.
      // taskuri paralel pornite care fac ambele uz de parallelStream se vor INFLUENTA reciproc. < vrei asta ?
      return result;
   }

   private List<String> resolve(List<Integer> firstHalf) {
      return firstHalf.stream().map(externalSystem::retrieveEmail)
//          .filter(email -> externalSystem.isEmailValid(email))
          .collect(toList());
   }

//   private List<String> validateEmails(List<String> emails) {
//      return emails.stream()
//          .filter(externalSystem::isEmailValid)
//          .collect(toList());
//   }

   private List<String> removeDuplicatesInsensitive(List<String> list) {
      LinkedHashMap<String, String> map = new LinkedHashMap<>();
      for (String email : list) {
         map.put(email.toLowerCase(), email);
      }
      return new ArrayList<>(map.values());
   }

   private List<String> concat(List<String> emails1, List<String> emails2) {
//      List<String> result = new ArrayList<String>();
//      result.addAll(emails1);
//      result.addAll(emails1);
      return Stream.concat(emails1.stream(), emails2.stream()).collect(toList());
   }
   //endregion

}