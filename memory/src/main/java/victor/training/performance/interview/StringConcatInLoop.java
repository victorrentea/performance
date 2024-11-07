package victor.training.performance.interview;

import org.jooq.lambda.Unchecked;
import victor.training.performance.util.PerformanceUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static victor.training.performance.util.PerformanceUtil.waitForEnter;

public class StringConcatInLoop {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();
      Stream<String> elements = fetchSomeData(); // from DB,File,HttpRequest payload

      waitForEnter();
      System.out.println("Start writing contents to file!");
      long t0 = System.currentTimeMillis();

     try (FileWriter writer = new FileWriter("out.txt")) {
        elements.forEach(Unchecked.consumer(writer::write));
     } // much better for GC. just pass data through

     // any large string you build it ends up on:
      // disk(file), DB(CLOB/BLOB), HTTP(Response)
      // Streamlining = pass data through instead of accumulating large memory objects
//      FileUtils.writeStringToFile(new File("out.txt"), s, UTF_8);

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      waitForEnter();
   }

   private static Stream<String> fetchSomeData() {
//      ResultSet rs;
//      while (rs.next()) {

//      Stream<String> linesStream = Files.lines(new File("in.txt").toPath());

      return IntStream.range(1, 200_000)
          .mapToObj(n -> "hahaha"); // 6 ch x 2 bytes = 12;
   }
//   interface MyEntityRep
}
