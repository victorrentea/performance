package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.waitForEnter;

public class StringConcatInLoop {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();
      List<String> elements = IntStream.range(1,200_000)
          .mapToObj(n -> "hahaha") // 6 ch x 2 bytes = 12
          .collect(toList());

      waitForEnter();
      System.out.println("Start writing contents to file!");
      long t0 = System.currentTimeMillis();

     try (FileWriter writer = new FileWriter("out.txt")) {
       for (String element : elements) {
         writer.write(element);
       }
     } // much better for GC. just pass data through

     // any large string you build it ends up on:
      // disk(file), DB(CLOB/BLOB), HTTP(Response)
      // Streamlining = pass data through instead of accumulating large memory objects
//      FileUtils.writeStringToFile(new File("out.txt"), s, UTF_8);

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      waitForEnter();
   }
}
