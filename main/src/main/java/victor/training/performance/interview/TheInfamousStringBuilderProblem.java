package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.FileWriter;
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

      // StringBuilder este pentru String
      // ce e ArrayList pentru []
//      StringBuilder s = new StringBuilder();
//      for (String element : elements) {
//         s.append(element);
//      }
//      FileUtils.writeStringToFile(new File("out.txt"), s.toString(), UTF_8);
      try (FileWriter fw = new FileWriter("out.txt")) {
         for (String element : elements) { // steamlinezi datele
               fw.write(element);
         }
      }

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      PerformanceUtil.waitForEnter();
   }
}
