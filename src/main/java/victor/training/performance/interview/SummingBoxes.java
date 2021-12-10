package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.List;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

public class SummingBoxes {

    public static void main(String[] args) {
        PerformanceUtil.printJfrFile();


        List<Long> list = LongStream.range(1, 10_000_0000)
				.boxed().collect(toList());

        // TODO how about memory efficiency?

        long t0 = System.currentTimeMillis();

        Long sum = studyThis(list);

        long t1 = System.currentTimeMillis();
        System.out.println(sum);
        System.out.println(t1 - t0);
    }

    private static Long studyThis(List<Long> list) {
        long sum = 0L;
        for (Long i : list) {
            sum += i;
        }
        return sum;
    }

}
