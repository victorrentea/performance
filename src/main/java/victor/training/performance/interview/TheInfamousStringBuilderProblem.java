package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      List<String> elements = IntStream.range(1, 50_000) // try 50K x 6 chars : see TLAB
          .mapToObj(n -> "hahaha")
          .collect(toList());

      PerformanceUtil.waitForEnter();
      System.out.println("Start writing contents!");
      long t0 = System.currentTimeMillis();

      StringBuilder s;
      try (FileWriter writer = new FileWriter("out.txt")) {

         // Morala: daca ai de adunat un string mare, varsa-l afara din heap
         // - in fisier
         // - pe HttpServletResponse.getWriter().write
         // - in CLOB/BLOB

//         PreparedStatement ps;
//         ps.setbina

         s = new StringBuilder();
         for (String element : elements) {
//            s+=
   //         s.append(element);
            writer.write(element);
         }
      }

      // StringBuilder este pentru String, ce este ArrayList pentru []
      //(se autoscaleaza in sus). adica ArrayList se mareste la fiecare add? !! NU.
      // ci doar cand epuizeaza CAPACITATEA

      // += ... 550 ms
      // StringBuilder ... 9 ms
//      FileUtils.writeStringToFile(new File("out.txt"), s.toString().toString());

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      PerformanceUtil.waitForEnter();

//      System.out.println("Size final " + s.length());
   }
}
