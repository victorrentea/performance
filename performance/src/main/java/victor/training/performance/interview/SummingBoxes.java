package victor.training.performance.interview;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.LongStream;

public class SummingBoxes {

    public static void main(String[] args) {
        long[] list = LongStream.range(1, 10_000_000)
				.toArray();

//        List<long>
//        long[];


        // 10M longuri in List<Long> sizeof= sizeof(array) + 10M x sizeof(Long)
        // = (10M-20M) * sizeof(pointer) + 8*10M
        // = 8 * 30M

        // 10M long in long[] ==> sizeof = 8 * 10M ( de 3x mai putin)


        //Set<Long>


//        !!! NU TINE DATE MASIVE PE FLUXURI LUGNI. Compacteaza-le, ca mai sus

        long t0 = System.currentTimeMillis();

        long sum = 0L;
        for (long i : list) {
            sum += i;
        }

        long t1 = System.currentTimeMillis();
        System.out.println(sum);
        System.out.println(t1 - t0);
    }

}
