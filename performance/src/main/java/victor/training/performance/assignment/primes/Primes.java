package victor.training.performance.assignment.primes;

import java.util.ArrayList;
import java.util.List;

public class Primes {

    public static final int N_MAX = 1000_000;

    public static void main(String[] args) {

        //System.out.println(getPrimes(1, 100));

        long t0 = System.currentTimeMillis();
        int expected = countPrimes(1, N_MAX);
        long t1 = System.currentTimeMillis();
        long dt0 = t1-t0;
        System.out.println("First run took " + (t1-t0));

        t0 = System.currentTimeMillis();
        int actual = countPrimesFaster(1,N_MAX);
        t1 = System.currentTimeMillis();
        long dt1 = t1-t0;
        System.out.println("Your implem took " + (t1-t0));

        if (actual != expected) {
            System.err.println("You have a bug: expected to find " + expected + " primes, but you returned " + actual);
        } else if (dt1 > dt0 / 2) {
            System.err.println("Your solution is not fast enough yet. Are you using all the available resources?");
        } else {
            System.out.println("CONGRATULATIONS!");
        }

    }

    public static int countPrimesFaster(long start, long end) {
        return 0; // TODO implement faster
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
        if (n == 1 || n == 2) {
            return true;
        }
        if (n % 2 == 0) {
            return false;
        }
        for (long i = 3; i < Math.sqrt(n); i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
