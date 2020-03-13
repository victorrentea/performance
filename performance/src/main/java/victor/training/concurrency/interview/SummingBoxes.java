package victor.training.concurrency.interview;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.LongStream;

public class SummingBoxes {

    public static void main(String[] args) {
        List<Long> list = LongStream.range(1, 10_000_000)
				.boxed().collect(toList());

        long t0 = System.currentTimeMillis();

        Long sum = 0L;
        for (Long i : list) {
            sum += i;
        }

        long t1 = System.currentTimeMillis();
        System.out.println(sum);
        System.out.println(t1 - t0);
    }

}
