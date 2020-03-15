//package victor.perf;
//
//import org.openjdk.jmh.annotations.*;
//
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//@State(Scope.Thread)
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.MICROSECONDS)
//@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
//@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
//@Fork(1)
//public class SetVsListTest {
//    private int addAndSum(Collection<Integer> list) {
//        for (int i = 0; i < 1000; i++) {
//            list.add(i);
//        }
//        int sum = 0;
//        for (Integer n : list) {
//            sum += n;
//        }
//        return sum;
//    }
//
//    @Benchmark
//    public int list1000() {
//        return addAndSum(new ArrayList<>());
//    }
//
//    @Benchmark
//    public int set1000() {
//        return addAndSum(new HashSet<>());
//    }
//
//
//}
//
