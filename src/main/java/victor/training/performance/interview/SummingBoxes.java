package victor.training.performance.interview;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.LongStream;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

public class SummingBoxes {

    public static void main(String[] args) {
        List<Long> list = LongStream.range(1, 10_000_000)
                .boxed()
                .collect(toList());

        long t0 = currentTimeMillis();

        Long sum = sumThemUp(list);

        long t1 = currentTimeMillis();
        System.out.println("sum = " + sum);
        System.out.println(t1 - t0);
    }




    // 1) nu ai acces pe cod (e vreo librarie ciudata) nu pot pune in ea t0 t1 @Timed log
    // 2) cand nu e vb de cateva apeluri babane REST/DB ci multe si un picutz cam lungi 0.02ms x 10k/sec =batchuri
    private static Long sumThemUp(List<Long> list) {
        // TODO Reduce run time by changing 1 character below 😊
        long sum = 0L;
        for (Long i : list) {
//            sum = Long.valueOf(sum.intValue() + i);
            sum += i;
        }
        return sum;
    }
}
