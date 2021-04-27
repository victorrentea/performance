package victor.perf;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;


@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class StringBuilderTest {
    public static final int N = 10_000;

//    @Benchmark
//    public String list10_plus() {
//        String s = "";
//        for (int i = 0; i < 10; i++) {
//            s+=" " + i;
//        }
//        return s.substring(1);
//    }
//    @Benchmark
//    public String list10_stringBuilder() {
//        StringBuilder s = new StringBuilder();
//        for (int i = 0; i < 10; i++) {
//            s.append(" ").append(i);
//        }
//        return s.substring(1);
//    }
//    @Benchmark
//    public String list100_plus() {
//        String s = "";
//        for (int i = 0; i < 100; i++) {
//            s+=" " + i;
//        }
//        return s.substring(1);
//    }
//    @Benchmark
//    public String list100_stringBuilder() {
//        StringBuilder s = new StringBuilder();
//        for (int i = 0; i < 100; i++) {
//            s.append(" ").append(i);
//        }
//        return s.substring(1);
//    }

    @Benchmark
    public String list10K_plus() {
        String s = "";
        for (int i = 0; i < N; i++) {
            s+=" fjdskfdskfjdsfkdsjfkdsjfskfjdskfdsjfkdsjf" + i;
        }
        return s.substring(1);
    }
//    @Benchmark
//    public String list10K_stringBuilder() {
//        StringBuilder s = new StringBuilder();
//        for (int i = 0; i < N; i++) {
//            s.append(" ").append(i);
//        }
//        return s.substring(1);
//    }

}
