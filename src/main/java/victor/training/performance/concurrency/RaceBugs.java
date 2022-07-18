package victor.training.performance.concurrency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;


@Slf4j
@RequiredArgsConstructor
public class RaceBugs {
   private final ExternalDependency dependency;

   // DONE Collect all emails with dependency#retrieveEmail(id) - takes time (networking) DONE
   // DONE Eliminate duplicated emails (case insensitive)
   // TODO Only allow emails for which true == dependency#isEmailValid(email) - takes time (networking)
   // TODO Avoid calling checkEmail twice for the same email pentru ca ne costa bani 0.001$/call

//   private final List<String> allEmails = new ArrayList<>();
//   private final Map<String, Boolean> emailValidity = new HashMap<>();
//
//   // chemata pe 2 threaduri, fiecare cu cate 10k de id-uri
//   private void doRetrieveEmailsCuDeToate(List<Integer> idsChunk) {
//      for (Integer id : idsChunk) {
//         String email = dependency.retrieveEmail(id);
//         synchronized (allEmails) {
//            if (allEmails.stream().noneMatch(e -> e.equalsIgnoreCase(email))) {
//               Boolean isValid = emailValidity.get(email.toUpperCase());
//               if (isValid == null) {
//                  isValid = dependency.isEmailValid(email);
//                  emailValidity.put(email.toUpperCase(), isValid);
//               }
//               if (isValid) {
//                  allEmails.add(email);
//               }
//            }
//         }
//      }
//   }

   private List<String> retrieveEmails(List<Integer> idsChunk) { // pe 2 threaduri
      return idsChunk.stream().map(dependency::retrieveEmail).collect(toList());
   }

   private List<String> eliminateDuplicates(List<String> toateEmailurile) {
      List<String> uniqueEmails = new ArrayList<>();
      for (String email : toateEmailurile) {
         if (uniqueEmails.stream().noneMatch(e -> e.equalsIgnoreCase(email)))
            uniqueEmails.add(email);
      }
      return uniqueEmails;
   }

   private List<String> validateEmailChunk(List<String> jumate) { // pe 2 threaduri
      return jumate.stream().filter(dependency::isEmailValid).collect(toList());
   }

   public static void main(String[] args) throws Exception {
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


      // TEMA: folositi parallelStream si pentru prima faza, ca in final (cu pool privat):
      CompletableFuture<List<String>> futureEmails1 = supplyAsync(() -> retrieveEmails(firstHalf));
      CompletableFuture<List<String>> futureEmails2 = supplyAsync(() -> retrieveEmails(secondHalf));

      CompletableFuture<List<String>> uniqueEmails = futureEmails1.thenCombine(futureEmails2, this::concatLists)
              .thenApply(this::eliminateDuplicates);

      List<String> nevalidate = uniqueEmails.get();

      // ruleaza pe N_CPU-1 thjreaduri, in commonPool global per JVM
      // PERICOL: 1: sunt prea multe (eu voiam 2)
      // PERICOL: 2: Thread pool starvation

      Stream<String> stream = nevalidate.parallelStream().filter(email -> {
         log.info("Validez " + email);
         return dependency.isEmailValid(email); // EVITI sa faci IO pe commonPool, pentru ca il poti starva
      });


      ForkJoinPool pool = new ForkJoinPool(2);

      return pool.submit(  () -> stream.collect(toList()) ).get();
      // ca sa poti executa parallelStream-il pe un pool al tau (dimensionat, privat),
      // trebuie sa apelezi operatia terminala a stream (eg collect()) sa ruleze pe un worker
      // thread al unui ForkJoinPool.


      // submit the 2 tasks
//      ExecutorService pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 500, MILLISECONDS, new SynchronousQueue<>());
//      Future<?> future1 = pool.submit(() -> doRetrieveEmailsCuDeToate(firstHalf));
//      Future<?> future2 = pool.submit(() -> doRetrieveEmailsCuDeToate(secondHalf));
//      log.debug("Tasks launched...");
//
//      // wait for the tasks to complete
//      future1.get();
//      future2.get();
//      return allEmails;
   }

   @NotNull
   private List<String> concatLists(List<String> e1, List<String> e2) {
      return Stream.concat(e1.stream(), e2.stream()).collect(toList());
   }
   //endregion

}