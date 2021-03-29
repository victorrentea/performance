package victor.training.performance.interview;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.LongStream;

public class SummingBoxes {

    public static void main(String[] args) {
        long[] arr = LongStream.range(1, 10_000_000)
            .toArray();
        // TODO how about memory efficiency?

        long t0 = System.currentTimeMillis();

        long sum = 0L;
        for (long i : arr) {
            sum += i;
        }

        long t1 = System.currentTimeMillis();
        System.out.println(sum);
        System.out.println(t1 - t0);
    }

}
