package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    String maiEficientCredDaNamMasurat = new StringBuilder()
        .append("a").append(1).append("b").toString();

    File file = new File("out.txt");
    try (var fos = new FileOutputStream(file)) {
      for (String element : elements) {
        fos.write(element.getBytes(UTF_8)); // streamlining data #raise
        // vars pa disk ca-i ieftin discu. nu tin in memorie monstrul
      }
      // tata lor:
      // repo.streamAll().map(toStringCeva).forEach(fos::write);
    }
//     FileUtils.writeStringToFile(file, s.toString(), UTF_8);

    System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
    waitForEnter();
  }
}
