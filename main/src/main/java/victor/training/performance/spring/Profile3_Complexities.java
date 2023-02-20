package victor.training.performance.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.IntStream;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping("/profile/cpu")
@RequiredArgsConstructor
public class Profile3_Complexities {

   public final List<String> SMALLER_LIST = generate(19_000);
   public final List<String> LARGER_LIST = generate(20_000);
   private static List<String> generate(int n) {
      System.out.printf("Generating shuffled sequence of %,d elements...%n", n);
      List<String> result = IntStream.rangeClosed(1, n)
              .mapToObj(i -> "A" + i)
              .collect(toList());
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
      Set<String> copy = new HashSet<>(SMALLER_LIST); // Optimized: created a hashSet to find elements to remove faster
      copy.removeAll(LARGER_LIST);
      return copy.size();
   }
}
