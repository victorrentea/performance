package victor.training.performance.completableFuture;

import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import victor.training.performance.completableFuture.util.PerformanceUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static victor.training.performance.completableFuture.ThreadPools.*;

@ExtendWith(MockitoExtension.class)
@Timeout(1)
@TestMethodOrder(MethodName.class)
public class ThreadPoolsTest {
    @Mock
    Dependency dependency;
    @InjectMocks
    ThreadPoolsSolved workshop;


    @Test
    public void p01_cpu() {
        TestUtils.CaptureThreadName captureThreadName = new TestUtils.CaptureThreadName();
        Mockito.when(dependency.cpuWork("a")).thenAnswer(captureThreadName.answer("A"));

        CompletableFuture<String> resultFuture = workshop.p01_cpu("a");

        assertThat(resultFuture.join()).isEqualTo("A");
        assertThat(captureThreadName.getThreadName()).contains("commonPool");
    }

    @Test
    public void p02_network_then_cpu() {
        TestUtils.CaptureThreadName cpuThreadCapture = new TestUtils.CaptureThreadName();
        TestUtils.CaptureThreadName networkThreadCapture = new TestUtils.CaptureThreadName();
        Mockito.when(dependency.blockNetwork()).thenAnswer(networkThreadCapture.answer("a"));
        Mockito.when(dependency.cpuWork("a")).thenAnswer(cpuThreadCapture.answer("A"));

        CompletableFuture<String> resultFuture = workshop.p02_network_then_cpu();

        assertThat(resultFuture.join()).isEqualTo("A");
        assertThat(cpuThreadCapture.getThreadName()).contains("commonPool");
        assertThat(networkThreadCapture.getThreadName()).contains("customExecutor");
    }
    @Test
    public void p03_cpu_then_cpu() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.cpuWork("s1")).thenAnswer(TestUtils.delayedAnswer(100, "r1"));
        Mockito.when(dependency.cpuWork("s2")).thenAnswer(TestUtils.delayedAnswer(100, "r2"));

        long t0 = currentTimeMillis();
        CompletableFuture<String> resultFuture = workshop.p03_cpu_then_cpu("s1","s2");
        assertThat(currentTimeMillis() - t0).isLessThan(50);

        String result = resultFuture.get();
        assertThat(currentTimeMillis() - t0)
                .describedAs("Should run sequential, not in parallel. supplyAsync(()->f()) starts f() in a separate thread.")
                .isGreaterThan(150);
        assertThat(result).isEqualTo("r1r2");
    }
    @Test
    public void p04_cpu_par_cpu() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.cpuWork("s1")).thenAnswer(TestUtils.delayedAnswer(100, "r1"));
        Mockito.when(dependency.cpuWork("s2")).thenAnswer(TestUtils.delayedAnswer(100, "r2"));

        long t0 = currentTimeMillis();
        CompletableFuture<String> resultFuture = workshop.p04_cpu_par_cpu("s1","s2");
        assertThat(currentTimeMillis() - t0).isLessThan(50);

        String result = resultFuture.get();
        assertThat(currentTimeMillis() - t0)
                .describedAs("Should run in parallel. Hint: thenCombine...")
                .isLessThan(150);
        assertThat(result).isEqualTo("r1r2");
    }
    @Test
    public void p05_combineAsync() {
        TestUtils.CaptureThreadName networkThreadCapture = new TestUtils.CaptureThreadName();
        Mockito.when(dependency.blockNetwork()).thenAnswer(networkThreadCapture.answer("net"));
        TestUtils.CaptureThreadName diskThreadCapture = new TestUtils.CaptureThreadName();
        Mockito.when(dependency.blockDisk()).thenAnswer(diskThreadCapture.answer("disk"));
        TestUtils.CaptureThreadName cpuThreadCapture = new TestUtils.CaptureThreadName();
        Mockito.when(dependency.cpuWork("net disk")).thenAnswer(cpuThreadCapture.answer("A"));

        CompletableFuture<String> resultFuture = workshop.p05_combineAsync();

        assertThat(resultFuture.join()).isEqualTo("A");
        assertThat(cpuThreadCapture.getThreadName()).contains("commonPool");
        assertThat(networkThreadCapture.getThreadName()).contains("customExecutor");
        assertThat(diskThreadCapture.getThreadName()).contains("customExecutor");
    }

    @Test
    public void p06_delayed() {
        CompletableFuture<String> resultFuture = workshop.p06_delayed();

        assertThat(resultFuture.isDone()).isFalse();
        PerformanceUtil.sleepMillis(200);
        assertThat(resultFuture.isDone()).isFalse();
        PerformanceUtil.sleepMillis(301);
        assertThat(resultFuture.isDone()).isTrue();
    }

    @Test
    public void p07_defaultAfterTimeout_inTime() {
        Mockito.when(dependency.blockNetwork()).thenAnswer(x -> {
            PerformanceUtil.sleepMillis(100);
            return "data";
        });
        CompletableFuture<String> resultFuture = workshop.p07_defaultAfterTimeout();
        assertThat(resultFuture.join()).isEqualTo("data");
    }
    @Test
    public void p07_defaultAfterTimeout_timeout() {
        Mockito.when(dependency.blockNetwork()).thenAnswer(x -> {
            PerformanceUtil.sleepMillis(600);
            return "data";
        });
        CompletableFuture<String> resultFuture = workshop.p07_defaultAfterTimeout();
        assertThat(resultFuture.join()).isEqualTo("default");
    }

}