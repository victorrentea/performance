package victor.perf;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class StringBuilderTest {
    public static final int N = 10_000;

    @Benchmark
    public String plus10K() {
        String s = "";
        for (int i = 0; i < N; i++) {
            s+=" " + i;
        }
        return s.substring(1);
    }
    @Benchmark
    public String plus100() {
        String s = "";
        for (int i = 0; i < 100; i++) {
            s+=" " + i;
        }
        return s.substring(1);
    }

    @Benchmark
    public String stringBuilder10K() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < N; i++) {
            s.append(" ").append(i);
        }
        return s.substring(1);
    }
    @Benchmark
    public String stringBuilder100() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            s.append(" ").append(i);
        }
        return s.substring(1);
    }

}
