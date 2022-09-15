package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();
      List<String> elements = IntStream.range(1, 50_000) // see TLAB
          .mapToObj(n -> "hahaha")
          .collect(toList());

      PerformanceUtil.waitForEnter();
      System.out.println("Start writing contents!");
      long t0 = System.currentTimeMillis();

      String s = thisCode(elements);
      FileUtils.writeStringToFile(new File("out.txt"), s, UTF_8);

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      PerformanceUtil.waitForEnter();
   }

   private static String thisCode(List<String> elements) {
      StringBuilder s = new StringBuilder();
      for (String element : elements) {
         s.append(element); // why is this better ?!
         // instead of allocating a new string every iteration, a stringBuilder just like ArrayList
         // allocates more capacity than currentyly needed. so append ===>(sometimes) results in allocation
      }
      return s.toString();
   }
}
