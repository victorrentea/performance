package victor.training.performance.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping("/profile/cpu")
@RequiredArgsConstructor
public class Profile3_Complexities {

   public final List<Long> SMALLER_LIST = generate(29_999);
   public final List<Long> LARGER_LIST = generate(30_000);
   private static List<Long> generate(int n) {
      System.out.printf("Generating shuffled sequence of %,d elements...%n", n);
      List<Long> result = LongStream.rangeClosed(1, n).boxed().collect(toList());
      Collections.shuffle(result);
      System.out.println("DONE");
      return result;
   }

   @GetMapping
   public String profileMe() {
      long t0 = currentTimeMillis();
      int count = countNew();
      long t1 = currentTimeMillis();
      return "Counted " + count + " new records in " + (t1-t0);
   }

   private  int countNew() {
      Set<Long> copy = new HashSet<>(SMALLER_LIST); // Optimized: created a hashSet to find elements to remove faster
      copy.removeAll(LARGER_LIST);
      return copy.size();
   }
}
