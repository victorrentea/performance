package victor.training.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;


public class RaceBugs {
   private static final Logger log = LoggerFactory.getLogger(RaceBugs.class);
   private static final Object LOCK = new Object();
   private final ExternalDependency dependency;

   // this pools shutdowns automatically at test end

   // ===================================================================
   private final List<String> uniqueEmails = new ArrayList<>();
   // TODO fix population++ race (warmup)
//   private Integer population = 0;
   private AtomicInteger population = new AtomicInteger(0);


   public RaceBugs(ExternalDependency dependency) {
      this.dependency = dependency;
   }

   public static void main(String[] args) throws Exception {
      List<Integer> ids = IntStream.range(0, 20_000).boxed().collect(toList());
//      new RaceBugs(null).countAliveInParallel(ids);
      new RaceBugs(new ExternalDependencyFake(20_000)
          .withOverlappingEmails()
      ).retrieveEmails(ids);
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
      return population.get();
   }


   // ================================================================

   // TODO Collect all emails with EmailFetcher.retrieveEmail(i)
   // TODO Avoid duplicated emails
   // TODO All email should be checked with EmailFetcher.checkEmail(email)
   // TODO Reduce the number of calls to checkEmail

   private void workHere(List<Integer> idsChunk) { // ran in 2 parallel threads ATENTIE
      for (Integer id : idsChunk) {
         // TODO if (dependency.isAlive(id)) { // returns 50% true  => call tests
//            population++;
         population.incrementAndGet();
      }
   }

   public Collection<String> retrieveEmails(List<Integer> ids) throws Exception {
      int len = ids.size();
      ExecutorService pool = new ThreadPoolExecutor(0, 2, 500, MILLISECONDS, new SynchronousQueue<>());
      Future<?> future1 = pool.submit(() -> doRetrieveEmails(new LinkedHashSet<>(ids.subList(0, len / 2))));
      Future<?> future2 = pool.submit(() -> doRetrieveEmails(new LinkedHashSet<>(ids.subList(len / 2, len))));
      log.debug("Bombs away...");
      future1.get();
      future2.get();

      CompletableFuture.supplyAsync(() -> retrieveEmails(firstHalf))
      CompletableFuture.supplyAsync(() -> retrieveEmails(secodnHalf))
          thenCombine(celalat -> (list1, list) -> merge si elimini duppl in 1 thread ca e in mem super fast)


      // LIsta de allEmail aici nu contine duplicate DAR nu au fost inca verificate.
      List<String> firstHalf = uniqueEmails.subList(0, uniqueEmails.size() / 2);
      List<String> secondHalf = uniqueEmails.subList(uniqueEmails.size() / 2, uniqueEmails.size());

      List<String> result = supplyAsync(() -> checkEmails(firstHalf), pool)
          .thenCombineAsync(supplyAsync(() -> checkEmails(secondHalf), pool), this::contact)
          .get();

      log.debug("Cate mailuri am strans ?" + result.size());
      return result;
   }

   private List<String> contact(List<String> l1, List<String> l2) {
      List<String> list = new ArrayList<>();
      list.addAll(l1);
      list.addAll(l2);
      return list;
   }

   public List<String> checkEmails(List<String> emails) {
      return emails.stream().filter(dependency::checkEmail).collect(toList());
   }


   private  void doRetrieveEmails(Set<Integer> idsChunk) {
      List<String> listaMea = new ArrayList<>();
      for (Integer id : idsChunk) {
         String email = dependency.retrieveEmail(id); // 50% overlap
         listaMea.add(email);
      }
      //return listaMea; inca si mai bine

      // mai bine sa te sync o sing  data per worker thread.
      synchronized (uniqueEmails) {
         for (String email : listaMea) {
            if (!uniqueEmails.contains(email)) {
                uniqueEmails.add(email);
            }

         }
      }
   }
}