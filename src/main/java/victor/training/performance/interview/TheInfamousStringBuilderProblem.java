package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      List<String> elements = IntStream.range(1, 50_000) // try 50K x 6 chars : see TLAB
          .mapToObj(n -> "hahaha").collect(toList());

      PerformanceUtil.waitForEnter();
      long t0 = System.currentTimeMillis();

      String s = "";
      for (String element : elements) {
         s += element;
      }
      FileUtils.writeStringToFile(new File("out.txt"), s);

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      PerformanceUtil.waitForEnter();
   }
}
