package victor.training.performance.interview;

import org.jooq.lambda.Unchecked;
import victor.training.performance.util.PerformanceUtil;

import java.io.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      Stream<String> elements = queryDinDBCareImiDaStream();

      File file = new File("out.txt");
      PerformanceUtil.waitForEnter();
      long t0 = System.currentTimeMillis();

      try (Writer writer = new BufferedWriter(new FileWriter(file))){
         elements.forEach(Unchecked.consumer(writer::write));

//         for (String element : elements) {
//            writer.write(element);
//         }
      }
//      StringBuilder sb = new StringBuilder();
//         for (String element : elements) {
//            sb.append(element);
//         }
//      FileUtils.writeStringToFile(file, sb.toString());

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      PerformanceUtil.waitForEnter();
   }

   private static Stream<String> queryDinDBCareImiDaStream() {
      Stream<String> elements = IntStream.range(1, 9_000_000_00) // try 50K x 6 chars : see TLAB 5M * 12 = 60MB
          .mapToObj(n -> "hahaha")
//          .collect(toList())
          ;
      return elements;
   }
}
