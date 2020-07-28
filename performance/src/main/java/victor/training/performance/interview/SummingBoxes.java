package victor.training.performance.interview;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.LongStream;

public class SummingBoxes {
//     77 68 72 71 76 60
    public static void main(String[] args) {
        List<Long> list = LongStream.range(1, 10_000_000)
				.boxed().collect(toList());

        long t0 = System.currentTimeMillis();
 //
        long sum = 0L;
//        for (Long i : list) {
        for (int i = 0; i < list.size() / 4; i++) {
            sum += list.get(i*4);
            sum += list.get(i*4 +1);
            sum += list.get(i*4+ 2);
            sum += list.get(i*4+ 3);
        }

        long t1 = System.currentTimeMillis();
        System.out.println(sum);
        System.out.println(t1 - t0);
    }

}
