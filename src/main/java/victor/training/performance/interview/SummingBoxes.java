package victor.training.performance.interview;

import java.util.List;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

public class SummingBoxes {

    public static void main(String[] args) {
        List<Long> list = LongStream.range(1, 10_000_000)
				.boxed()
            .collect(toList());

        // TODO how about memory efficiency?

        long t0 = System.currentTimeMillis();

        Long sum = 0L;
        for (Long i : list) {
            sum += i;
        }

        long t1 = System.currentTimeMillis();
        System.out.println("sum = " + sum);
        System.out.println(t1 - t0);
    }

}
