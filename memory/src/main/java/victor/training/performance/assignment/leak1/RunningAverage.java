package victor.training.performance.assignment.leak1;

import lombok.Data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.RandomStringUtils.random;

public class RunningAverage {
   @Data
   static class SomeElement {
      private Long id = 1000L;
      private int age = 10 + new Random().nextInt(80);
      private String field1 = random(20, true, true);
      private String field2 = random(20, true, true);
      private String field3 = random(20, true, true);
      private List<SomeChild> list = IntStream.range(0,10).mapToObj(i -> new SomeChild()).collect(toList());
   }
   @Data
   static class SomeChild {
      private String name = random(20, true, false);
      private String data = random(20, true, false);
   }

   public static void main(String[] args) throws IOException {
      Stream<List<SomeElement>> pageStream = Stream.generate(RunningAverage::generatePage);

      Writer writer = new FileWriter("out.tmp"); // imagine writing to a file
      averageWindow(() -> pageStream.limit(2_000_000).iterator(), 10_000, writer);
   }

   private static List<SomeElement> generatePage() {
      return Stream.generate(SomeElement::new).limit(500).collect(toList());
   }

   private static void averageWindow(Iterable<List<SomeElement>> numbersInPages, int windowSize, Writer writer) throws IOException {
      List<SomeElement> window = new ArrayList<>();
      int totalSeen = 0;

      for (List<SomeElement> page : numbersInPages) {
         totalSeen += page.size();
         window.addAll(page);
         if (window.size() > windowSize) {
            window = window.subList(window.size() - windowSize, window.size());
         }
         double avgAge = window.stream().mapToInt(SomeElement::getAge).average().getAsDouble();
         double avgChildCount = window.stream().mapToInt(e -> e.getList().size()).average().getAsDouble();
         writer.write("avgAge="+avgAge + ",avgChildren=" + avgChildCount);
         if (totalSeen % 10_000 == 0) {
            System.out.println("Processed " + totalSeen + " items");
         }
      }
      System.out.println("No OutOfMemoryOccured! Have you set -mx ? Anyway: take a heapdump and investigate");
      new Scanner(System.in).nextLine();
   }
}
