package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import victor.training.performance.util.PerformanceUtil;

import java.io.*;
import java.util.List;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();
      List<String> elements = IntStream.range(1, 1000_000) // see TLAB
          .mapToObj(n -> "hahaha")
          .collect(toList());

      PerformanceUtil.waitForEnter();
      System.out.println("Start writing contents!");

//      StringBuilder s = new StringBuilder(); // resizable mutable string with extra capacity
//      s = "Text " + args + " more text " + t0 + "more" + elements + "more";
//      s = new StringBuilder().append("Text ").append(args).append(" more text ").append(t0).append("more").append(elements).append("more").toString();
//      for (String element : elements) {
//         s.append(element);
//      }
//      FileUtils.writeStringToFile(new File("out.txt"), s.toString(), UTF_8);
long t0;
         t0 = System.currentTimeMillis();
      try (Writer fw = new BufferedWriter(new FileWriter("out.txt"))) {
         for (String element : elements) {
            fw.write(element);
         }
      }

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      PerformanceUtil.waitForEnter();
   }
}
