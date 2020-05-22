package victor.training.performance.java8;

import victor.training.performance.ConcurrencyUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.ConcurrencyUtil.log;

public class ParallelStreams {

    public static void main(String[] args) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "3"); // by def paralleismul common pool = NCore -1
        List<Integer> numbers = IntStream.range(1, 11).boxed().collect(toList());


        numbers.parallelStream()
                .filter(n -> {
                    log("Filtrez " + n);
                    return n % 2 == 1;
                })
                .map(n -> {
                    log("Square " + n);
                    return n * n;
                })
                .forEach(n -> log("OUT: " + n));
        ;
    }
}
