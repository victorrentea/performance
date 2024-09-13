package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import victor.training.performance.util.PerformanceUtil;

import java.io.*;
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

//      String s = "";
//      for (String element : elements) {
//         s += element;
//      } // use StringBuilder
      // but it is more mem officient to drop the memory on disk ASAP
      // "streamlining data"
      try(Writer writer = new BufferedWriter(new FileWriter("out.txt"))) {
         for (String element : elements) {
            writer.write(element);
         }
         //better: stream teh data from where it came (DB, DISK)
         // or paginate the data
         // and dump the data on the output (file/socket)
      }

//      FileUtils.writeStringToFile(new File("out.txt"), s, UTF_8);

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      waitForEnter();
   }
}
