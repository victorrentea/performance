//package victor.perf;
//
//import lombok.SneakyThrows;
//import org.apache.commons.io.IOUtils;
//import org.jooq.lambda.Unchecked;
//import org.openjdk.jmh.annotations.*;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.Writer;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//
//@State(Scope.Thread)
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.MICROSECONDS)
//@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
//@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
//@Fork(1)
//public class IOParallel {
//    private ExecutorService pool10;
//    private ExecutorService pool100;
//    private char[] data = new char[100*1024];
//    private File tmp;
//
//    @Setup
//    public void startThreadPool() {
//        tmp = new File("tmp");
//        tmp.mkdirs();
//        pool10 = Executors.newFixedThreadPool(10);
//        pool100 = Executors.newFixedThreadPool(100);
//    }
//
//    @Benchmark
//    public int pool10() {
//        List<Future<Integer>> futures = new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//            int j=i;
//            futures.add(pool10.submit(() -> writeFile(j)));
//        }
//        futures.forEach(Unchecked.consumer(Future::get));
//        return 1;
//    }
//
//    @SneakyThrows
//    private int writeFile(int i) {
//        try (Writer writer = new FileWriter(new File(tmp, "file+" + i + ".dat"))) {
//            IOUtils.write(data, writer);
//        }
//        return 1;
//    }
//
//    @Benchmark
//    public int set1000() {
//        List<Future<Integer>> futures = new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//            int j=i;
//            futures.add(pool100.submit(() -> writeFile(j)));
//        }
//        futures.forEach(Unchecked.consumer(Future::get));
//        return 1;    }
//
//    @TearDown
//    public void closePools() throws InterruptedException {
//        pool10.shutdown();
//        pool100.shutdown();
//        pool10.awaitTermination(1, TimeUnit.SECONDS);
//        pool100.awaitTermination(1, TimeUnit.SECONDS);
//    }
//
//
//}
//
