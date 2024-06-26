package victor.training.performance.concurrency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;


@Slf4j
@RequiredArgsConstructor
public class RaceBugsHard {
   private final ExternalDependency dependency;

   // TODO Collect all emails with dependency#retrieveEmail(id) - !!takes time (networking) - parallelize
   // TODO Eliminate duplicated emails (case insensitive!)
   // TODO Only allow emails for which dependency#isEmailValid(email)==true - !! takes time (networking) - parallelize
   // TODO Avoid calling checkEmail twice for the same email (it costs $,Δt)

   private final List<String> allEmails = new ArrayList<>();

   // called by two parallel threads
   private void doRetrieveEmails(List<Integer> idsChunk) {
      for (Integer id : idsChunk) { // size() = 10K
         String email = dependency.retrieveEmail(id);
         allEmails.add(email);
      }
   }

   public static void main(String[] args) throws Exception {
      ExternalDependencyFake dependency = new ExternalDependencyFake(20_000);
      List<Integer> ids = IntStream.range(0, 20_000).boxed().collect(toList());
      Collection<String> results = new RaceBugsHard(dependency).retrieveEmailsInParallel(ids);

      log.debug("No of emails returned: " + results.size());
      // log.debug("No of emails checked: " + dependency.emailChecksPerformed());
   }

   //region SameCodeAsInIntro
   public Collection<String> retrieveEmailsInParallel(List<Integer> ids) throws Exception {
      // split the work in two
      List<Integer> firstHalf = ids.subList(0, ids.size() / 2);
      List<Integer> secondHalf = ids.subList(ids.size() / 2, ids.size());

      // submit the 2 tasks
      ExecutorService pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 500, MILLISECONDS, new SynchronousQueue<>());
      Future<?> future1 = pool.submit(() -> doRetrieveEmails(firstHalf));
      Future<?> future2 = pool.submit(() -> doRetrieveEmails(secondHalf));
      log.debug("Tasks launched...");

      // wait for the tasks to complete
      future1.get();
      future2.get();
      return allEmails;
   }
   //endregion

}