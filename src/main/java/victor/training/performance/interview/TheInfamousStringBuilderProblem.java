package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      List<String> elements = IntStream.range(1, 50_000_000) // try 50K x 6 chars : see TLAB
          .mapToObj(n -> "hahaha")
          .collect(toList());

      PerformanceUtil.waitForEnter();
      System.out.println("Start writing contents!");
      long t0 = System.currentTimeMillis();

      // DE AICI IN JOS

//      StringBuilder s = new StringBuilder();
      try (FileWriter writer = new FileWriter(new File("out.txt"))) {
         for (String element : elements) {
   //         s.append(element);
            writer.write(element);
         }
      }
//      FileUtils.writeStringToFile(new File("out.txt"), s.toString());

      // PANA AICI
      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      PerformanceUtil.waitForEnter();
   }
}
