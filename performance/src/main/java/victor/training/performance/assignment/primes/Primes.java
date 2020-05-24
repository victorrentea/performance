package victor.training.performance.assignment.primes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Primes {
    public static void main(String[] args) {

        System.out.println(getPrimes(1, 100));
        long t0 = System.currentTimeMillis();
        System.out.println(countPrimesParallel(1,1000_000));
        long t1 = System.currentTimeMillis();
        System.out.println("took " + (t1-t0));
    }

    public static int countPrimesParallel(long start, long end) {
        List<Future<Integer>> segments = new ArrayList<>();
        for (long startSeg1 = start; startSeg1 < end; startSeg1 += 5000) {
            long startSeg = startSeg1;
            long endSeg = Math.min(end, startSeg + 5000);
            segments.add(CompletableFuture.supplyAsync(() -> countPrimes(startSeg, endSeg)));
        }
        return segments.stream().map(integerFuture -> {
            try {
                return integerFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).mapToInt(Integer::intValue).sum();
    }

    public static int countPrimes(long start, long end) {
        int count = 0;
        for (long n = start; n < end; n++) {
            if (isPrime(n)) {
                count++;
            }
        }
        return count;
    }
    public static List<Long> getPrimes(long start, long end) {
        List<Long> list = new ArrayList<>();
        for (long n = start; n <= end; n++) {
            if (isPrime(n)) {
                list.add(n);
            }
        }
        return list;
    }


    public static boolean isPrime(long n) {
        if (n == 1 || n % 2 == 0) {
            return true;
        }
        for (long i = 3; i < Math.sqrt(n); i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
