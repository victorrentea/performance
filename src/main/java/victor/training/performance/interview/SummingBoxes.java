package victor.training.performance.interview;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

public class SummingBoxes {

    private static final Logger log = LoggerFactory.getLogger(SummingBoxes.class);

    public static void main(String[] args) {
        List<Long> list = LongStream.range(1, 100_000_000)
				.boxed()
            .collect(toList());

        // TODO how about memory efficiency?

        long t0 = System.currentTimeMillis();

        Long sum = deStudiat(list);

        long t1 = System.currentTimeMillis();
        System.out.println("sum = " + sum);

        log.debug("a {}", list);
        System.out.println(t1 - t0);
    }

    @NotNull
    private static Long deStudiat(List<Long> list) {
        Long sum = 0L;
        for (Long i : list) {
//            sum += i;
            sum = Long.valueOf(sum.longValue() + i);
        }
        return sum;
    }

}
