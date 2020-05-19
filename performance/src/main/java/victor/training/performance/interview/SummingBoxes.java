package victor.training.performance.interview;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.LongStream;

public class SummingBoxes {

    public static void main(String[] args) {
        List<Long> list = LongStream.range(1, 10_000_000)
				.boxed().collect(toList());
        long[] arr = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        // TODO iterarea pe arr evita si unboxingul.

        long t0 = System.currentTimeMillis();

        long sum = 0L;
        for (Long i : list) {
//            sum = new Long(i.longValue() + sum.longValue());
//            sum = sum + i.longValue(); // unboxed
            sum += i;
        }

        long t1 = System.currentTimeMillis();
        System.out.println(sum);
        System.out.println(t1 - t0);
    }

}
