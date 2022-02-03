package victor.training.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


public class RaceBugs {
   private static final Logger log = LoggerFactory.getLogger(RaceBugs.class);

   private final ExternalDependency dependency;
   public RaceBugs(ExternalDependency dependency) {
      this.dependency = dependency;
   }

   // this pools shutdowns automatically at test end

   // ===================================================================

   public static void main(String[] args) throws Exception {
      List<Integer> ids = IntStream.range(0, 20_000).boxed().collect(toList());
      new RaceBugs(null).countAliveInParallel(ids);
   }

   public int countAliveInParallel(List<Integer> ids) throws Exception {
      int len = ids.size();
      ExecutorService pool = new ThreadPoolExecutor(0, 2, 500, MILLISECONDS, new SynchronousQueue<>());
      Future<?> future1 = pool.submit(() -> workHere(ids.subList(0, len / 2)));
      Future<?> future2 = pool.submit(() -> workHere(ids.subList(len / 2, len)));
      log.debug("Tasks started...");
      future1.get();
      future2.get();
      log.debug("Counted {} ids", population);
      return population;
   }


   // TODO fix population++ race (warmup)
   private Integer population = 0;

   private void workHere(List<Integer> idsChunk) { // ran in 2 parallel threads
      for (Integer id : idsChunk) {
            // TODO if (dependency.isAlive(id)) { // returns 50% true  => call tests
            population++;
      }
   }



   // ================================================================

   // TODO Collect all emails with EmailFetcher.retrieveEmail(i)
   // TODO Avoid duplicated emails
   // TODO All email should be checked with EmailFetcher.checkEmail(email)
   // TODO Reduce the number of calls to checkEmail

   public Collection<String> retrieveEmails(List<Integer> ids) throws Exception {
      int len = ids.size();
      ExecutorService pool = new ThreadPoolExecutor(0, 2, 500, MILLISECONDS, new SynchronousQueue<>());
      Future<?> future1 = pool.submit(() -> doRetrieveEmails(ids.subList(0, len / 2)));
      Future<?> future2 = pool.submit(() -> doRetrieveEmails(ids.subList(len / 2, len)));
      log.debug("Bombs away...");
      future1.get();
      future2.get();
      return allEmails;
   }
   private final List<String> allEmails = new ArrayList<>();
   private void doRetrieveEmails(List<Integer> idsChunk) {
      for (Integer id : idsChunk) {
         String email = dependency.retrieveEmail(id);
         if (allEmails.contains(email)) {
            continue;
         }
         if (dependency.checkEmail(email))
            allEmails.add(email);
      }
   }
}