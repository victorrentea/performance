package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import victor.training.performance.PerformanceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      List<String> elements = IntStream.range(1, 100_000) // try 50K x 6 chars : see TLAB
          .mapToObj(n -> "hahaha").collect(toList());

      PerformanceUtil.waitForEnter();
      long t0 = System.currentTimeMillis();


      String s = met(elements);


      FileUtils.writeStringToFile(new File("out.txt"), s);

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0) + " final string = "+s.length());
      PerformanceUtil.waitForEnter();
   }

   private static String met(List<String> elements) {
      String s = ""; // pe method stack frame, cat ocupa var s = 4octeti (64bit)
      // daca stringul are lungimea 10M

      for (String element : elements) { // daca sunt 10-100 : impactul e ns
         s += element;
      }
      return s;
   }
}
class OClasaOrarecareFaraHashCode {
   @Override
   public int hashCode() {
      return 1;
   }
}
//      int refUnica = System.identityHashCode(s);
//      System.out.println("Pointer:" +refUnica);
//
//      OClasaOrarecareFaraHashCode inst = new OClasaOrarecareFaraHashCode();
//      System.out.println(inst);
//      System.out.println(inst.hashCode());
//      System.out.println(Integer.toHexString(System.identityHashCode(inst)));
