package victor.training.performance.java8;

import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ParallelStreams {
   public static void main(String[] args) {
      asList(1,2 ).stream().collect(Collectors.groupingByConcurrent());
   }
}
