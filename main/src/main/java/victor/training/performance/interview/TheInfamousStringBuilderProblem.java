package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();
      List<String> elements = IntStream.range(1, 500_000) // see TLAB
          .mapToObj(n -> "hahaha") // 2 x 6 x 50K = 6000KB
          .collect(toList());

      PerformanceUtil.waitForEnter();
      System.out.println("Start writing contents!");
      long t0 = System.currentTimeMillis();

//      String s = "";
//      for (String element : elements) {
//         s += element; // la fiecare iteratie se face malloc
//      }
      StringBuilder s = new StringBuilder();
      // StringBuilder e pentru String ce ArrayList e pentru []
      // = prealoca mai multa capacitate decat tine date acum
      // pt a appenda mai ieftin. din cand in cand tre sa se dubleze
      for (String element : elements) {
         s.append(element); // 19 ms fata de 32 SECUNDE
      }
      FileUtils.writeStringToFile(new File("out.txt"), s.toString(), UTF_8);

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      PerformanceUtil.waitForEnter();
   }
}
