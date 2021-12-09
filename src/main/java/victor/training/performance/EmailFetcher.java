package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class EmailFetcher {

   private static final List<String> ALL_EMAILS = new ArrayList<>();
   public static AtomicInteger emailChecksCounter = new AtomicInteger(0);

   static {
      List<String> list = IntStream.range(0, RaceBugs.N / 2)
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

   public static boolean checkEmailExpen$ive(String email) {
      emailChecksCounter.incrementAndGet();
      PerformanceUtil.sleepSomeTime(0, 1); // network call
      return true;
   }
}
