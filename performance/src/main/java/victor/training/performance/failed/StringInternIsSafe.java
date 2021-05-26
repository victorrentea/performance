package victor.training.performance.failed;

import org.apache.commons.lang.StringUtils;
import org.jooq.lambda.Unchecked;
import victor.training.performance.PerformanceUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.stream.Stream;

public class StringInternIsSafe {
   public static void main(String[] args) throws IOException {

      Stream<Path> allFilesInProject = Files.find(new File(".").toPath(), 999,
          (p, bfa) -> bfa.isRegularFile() && p.toFile().getAbsolutePath().endsWith(".java"));
      Random r = new Random();
      int sum = allFilesInProject
          .flatMap(Unchecked.function(f -> Files.lines(f)))
          .map(s -> StringUtils.repeat(s, 10000) + r.nextInt()) // exagerate a bit
          .map(String::intern) // From Java 7+, unreferenced Strings from  String Common Pool are GCed
          .mapToInt(String::length)
          .sum();

      System.out.println(sum);
      System.out.println("Take a look at the heap");
      PerformanceUtil.waitForEnter();
   }
}
