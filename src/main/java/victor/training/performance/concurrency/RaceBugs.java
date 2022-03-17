package victor.training.performance.concurrency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

      CompletableFuture<List<String>> firstHalfFuture = supplyAsync(() -> resolve(firstHalf));
      CompletableFuture<List<String>> secondHalfFuture = supplyAsync(() -> resolve(secondHalf));

      List<String> toate = firstHalfFuture.thenCombine(secondHalfFuture, this::concat).get();
      List<String> distinctEmails = removeDuplicatesInsensitive(toate);


      return distinctEmails;
   }

   private List<String> resolve(List<Integer> firstHalf) {
      return firstHalf.stream().map(externalSystem::retrieveEmail)
          .filter(email -> externalSystem.isEmailValid(email))
          .collect(toList());
   }

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