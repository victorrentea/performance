package victor.training.performance.interview;

import lombok.SneakyThrows;
import victor.training.performance.PerformanceUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      List<String> elements = IntStream.range(1, 1000_000) // try 50K x 6 chars : see TLAB
          .mapToObj(n -> "hahaha").collect(toList());

      PerformanceUtil.waitForEnter();
      long t0 = System.currentTimeMillis();


      try (FileWriter writer = new FileWriter(new File("out.txt"))) {
         met(elements, writer);
      }


      Queue<Integer> q = new LinkedList<>();


      System.out.println("Done. Took " + (System.currentTimeMillis() - t0) + " final string = ");
      PerformanceUtil.waitForEnter();
   }

   @SneakyThrows
   private static void met(List<String> elements, FileWriter writer) {
      for (String element : elements) { // daca sunt 10-100 : impactul e ns
         writer.write(element);
      }
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
