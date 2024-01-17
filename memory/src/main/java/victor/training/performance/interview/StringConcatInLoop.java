package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.waitForEnter;

public class StringConcatInLoop {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();
      List<String> elements = IntStream.range(1, 50_000)
          .mapToObj(n -> "hahaha") // 6 ch x 2 bytes = 12
          .collect(toList());

      waitForEnter();
      System.out.println("Start writing contents to file!");
      long t0 = System.currentTimeMillis();

      String s = interviu(elements);

      FileUtils.writeStringToFile(new File("out.txt"), s, UTF_8);

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0) + "." + args); // perfect ok din java 8+
      // javac pune un cod optimizat aici
      waitForEnter();
   }

   private static String interviu(List<String> elements) {
      StringBuilder s = new StringBuilder(); // de ce repara asta problema?
      // in loc sa aloce un string la fiecare iteratie,
      // string builder este un string redimensionabil,
      // StringBuilder este pentru String ce ArrayList este pentru []
      for (String element : elements) {
         s.append(element).append(",");
      }
      return s.toString();
   }
}
