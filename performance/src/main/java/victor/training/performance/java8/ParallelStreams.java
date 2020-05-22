package victor.training.performance.java8;

import victor.training.performance.ConcurrencyUtil;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleep2;

public class ParallelStreams {

    public static void main(String[] args) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "3"); // by def paralleismul common pool = NCore -1
        List<Integer> numbers = IntStream.range(1, 11).boxed().collect(toList());

//        Iterable<Integer> iterable = numbers::iterator;
//        Stream<Integer> stream = StreamSupport.stream(iterable.spliterator(), false);

        numbers.parallelStream()
                .filter(n -> {
                    log("Filtrez " + n);
                    return n % 2 == 1;
                })
                .map(n -> {
                    log("Square " + n);
                    sleep2(1000); // chemi un WS, citesti un fisier, apelezi DB
                    return n * n;
                })
                .forEach(n -> log("OUT: " + n));
        ;
    }
}
