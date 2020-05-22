//package victor.perf;
//
//import org.openjdk.jmh.annotations.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@State(Scope.Thread)
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.MICROSECONDS)
//@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
//@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
//@Fork(1)
//public class FizzBuzzTest {
//    @Benchmark
//    public String original() {
//        return FizzFuzzOriginal.f().toString();
//    }
//    @Benchmark
//    public String optimized() {
//        return FizzFuzzOptimized.f().toString();
//    }
//
//
//}
//
//class FizzFuzzOptimized {
//    public static List<String> f() {
//        List<String> result = new ArrayList<>();
//        for (int i = 1; i < 100; i++) {
//            String temp = Integer.toString(i);
//            if (i % 15 == 0)
//                temp = "fizz fuzz";
//            else if (i % 5 == 0)
//                temp = "fuzz";
//            else if (i % 3 == 0)
//                temp = "fizz";
//            result.add(temp);
//        }
//        return result;
//    }
//}
//
//
//class FizzFuzzOriginal {
//    public static List<String> f() {
//        int[] divisors = { 3, 5, 15 };
//        String[] messages = { "fizz", "fuzz", "fizz fuzz" };
//        FizzFuzzOriginal fuzz = new FizzFuzzOriginal(1, 100);
//        return fuzz.printNumbersDivisibleByN(divisors, messages);
//    }
//
//    int lowerRange, upperRange;
//
//    public FizzFuzzOriginal(int lowerRange, int upperRange) {
//        this.lowerRange = lowerRange;
//        this.upperRange = upperRange;
//    }
//
//    public int getLowerRange() {return this.lowerRange;}
//    public int getUpperRange() {return this.upperRange;}
//
//    public List<String> printNumbersDivisibleByN(int[] n, String[] messages) {
//        List<String> result = new ArrayList<>();
//        if ((n == null) || (messages == null)) return result;
//        for (int i = getLowerRange(); i < getUpperRange(); i++) {
//            String messageToPrint = fizzFuzz(i, n, messages);
//            result.add(messageToPrint);
//        }
//        return result;
//    }
//
//    private String fizzFuzz(int value, int[] div, String[] messages) {
//        if ((div == null) || (messages == null)) return "";
//        int index = div.length;
//        while (--index >= 0)
//            if ((value % div [index]) == 0)
//                return messages[index];
//        return Integer.toString(value);
//    }
//}
