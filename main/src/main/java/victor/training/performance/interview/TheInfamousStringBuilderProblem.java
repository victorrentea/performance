package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class TheInfamousStringBuilderProblem {
//      String s = elements.stream().map(element -> element.toUpperCase() + ",").collect(Collectors.joining());
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();
      List<String> elements = IntStream.range(1, 50_000) // see TLAB
          .mapToObj(n -> "hahaha")
          .collect(toList());

      PerformanceUtil.waitForEnter();
      System.out.println("Start writing contents!");
      long t0 = System.currentTimeMillis();

//      String s = "";
//      for (String element : elements) {
//         s += element.toUpperCase() + ","; // +1 malloc / loop
//      }
//      FileUtils.writeStringToFile(new File("out.txt"), s, UTF_8);

      try (FileWriter fileWriter = new FileWriter(new File("out.txt"))) {
         for (String element : elements) {
            fileWriter.write(element.toUpperCase() + ",");
         }
      }

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      PerformanceUtil.waitForEnter();
   }
  // StringBuilder is more efficient in a loop
   // for the same reasons ArrayList is more efficient than [] - huh?!
   // has extra capacity available wihch avoids reallocations

   public String stringBuilderAutomatiicallySinceJava7(int a, int b) {
      return "some " + a + " jat " + b;
   }
}
