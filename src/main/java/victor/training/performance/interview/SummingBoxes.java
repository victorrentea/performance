package victor.training.performance.interview;

import java.util.stream.LongStream;

public class SummingBoxes {

    public static void main(String[] args) {
        double[] list = LongStream.range(1, 10_000_000).mapToDouble(d->d)
				.toArray();

        // TODO how about memory efficiency?

        long t0 = System.currentTimeMillis();

        Double sum = logicaMeaDeProd(list);

        long t1 = System.currentTimeMillis();
        System.out.println(sum);
        System.out.println(t1 - t0);
    }

    private static Double logicaMeaDeProd(double[] list) {
        double sum = 0d;
        for (double d : list) {
            sum += d;
        }
        return sum;
    }

}
