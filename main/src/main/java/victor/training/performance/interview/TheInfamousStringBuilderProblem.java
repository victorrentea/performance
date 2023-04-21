package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import org.jooq.lambda.Unchecked;
import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.jooq.lambda.Unchecked.consumer;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();
      Stream<String> elements = IntStream.range(1, 200_000) // see TLAB
          .mapToObj(n -> "hahaha")
          ;

      PerformanceUtil.waitForEnter();
      System.out.println("Start writing contents!");
      long t0 = System.currentTimeMillis();
//      GZIPInputStream s = new GZIPInputStream(new FileInputStream("the.zip"));
      // ---- start: optimize below:
      try (FileWriter writer = new FileWriter(new File("out.txt"))) {
         elements.forEach(consumer(writer::write));
      }

      //      FileUtils.writeStringToFile(new File("out.txt"), s.toString(), UTF_8);
      // ---- end

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      PerformanceUtil.waitForEnter();
   }
}
