package victor.training.performance.java8.cf;

import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import victor.training.performance.java8.cf.TestUtils.CaptureThreadName;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static victor.training.performance.java8.cf.ThreadPools.*;

@ExtendWith(MockitoExtension.class)
@Timeout(1)
@TestMethodOrder(MethodName.class)
public class ThreadPoolsTest {
    @Mock
    Dependency dependency;
    @InjectMocks
    ThreadPools workshop;


    @Test
    public void p01_cpu() {
        CaptureThreadName captureThreadName = new CaptureThreadName();
        when(dependency.cpuWork("a")).thenAnswer(captureThreadName.answer("A"));

        CompletableFuture<String> resultFuture = workshop.p01_cpu("a");

        assertThat(resultFuture.join()).isEqualTo("A");
        assertThat(captureThreadName.getThreadName()).contains("commonPool");
    }

    @Test
    public void p02_network_then_cpu() {
        CaptureThreadName cpuThreadCapture = new CaptureThreadName();
        CaptureThreadName networkThreadCapture = new CaptureThreadName();
        when(dependency.network()).thenAnswer(networkThreadCapture.answer("a"));
        when(dependency.cpuWork("a")).thenAnswer(cpuThreadCapture.answer("A"));

        CompletableFuture<String> resultFuture = workshop.p02_network_then_cpu();

        assertThat(resultFuture.join()).isEqualTo("A");
        assertThat(cpuThreadCapture.getThreadName()).contains("commonPool");
        assertThat(networkThreadCapture.getThreadName()).contains("mypool");
    }
    @Test
    public void p03_combineAsync() {
        CaptureThreadName networkThreadCapture = new CaptureThreadName();
        when(dependency.network()).thenAnswer(networkThreadCapture.answer("net"));
        CaptureThreadName diskThreadCapture = new CaptureThreadName();
        when(dependency.disk()).thenAnswer(diskThreadCapture.answer("disk"));
        CaptureThreadName cpuThreadCapture = new CaptureThreadName();
        when(dependency.cpuWork("net disk")).thenAnswer(cpuThreadCapture.answer("A"));

        CompletableFuture<String> resultFuture = workshop.p03_combineAsync();

        assertThat(resultFuture.join()).isEqualTo("A");
        assertThat(cpuThreadCapture.getThreadName()).contains("commonPool");
        assertThat(networkThreadCapture.getThreadName()).contains("mypool");
        assertThat(diskThreadCapture.getThreadName()).contains("mypool");
    }

    @Test
    public void p04_delayed() {
        CompletableFuture<String> resultFuture = workshop.p04_delayed();

        assertThat(resultFuture.isDone()).isFalse();
        PerformanceUtil.sleepMillis(200);
        assertThat(resultFuture.isDone()).isFalse();
        PerformanceUtil.sleepMillis(301);
        assertThat(resultFuture.isDone()).isTrue();
    }

    @Test
    public void p05_defaultAfterTimeout_inTime() {
        when(dependency.network()).thenAnswer(x -> {
            PerformanceUtil.sleepMillis(100);
            return "data";
        });
        CompletableFuture<String> resultFuture = workshop.p05_defaultAfterTimeout();
        assertThat(resultFuture.join()).isEqualTo("data");
    }
    @Test
    public void p05_defaultAfterTimeout_timeout() {
        when(dependency.network()).thenAnswer(x -> {
            PerformanceUtil.sleepMillis(600);
            return "data";
        });
        CompletableFuture<String> resultFuture = workshop.p05_defaultAfterTimeout();
        assertThat(resultFuture.join()).isEqualTo("default");
    }

}