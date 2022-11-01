package victor.training.performance.completableFuture;

import org.mockito.stubbing.Answer;
import victor.training.performance.util.PerformanceUtil;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TestUtils {

    public static <T> Answer<T> delayedAnswer(int millis, T value) {
        return x -> {
            PerformanceUtil.sleepMillis(millis);
            return value;
        };
    }

    static class CaptureThreadName implements BiConsumer<Object,Object>, Consumer<Object>, Runnable {

        private String threadName;
        @Override
        public void accept(Object o, Object o2) {
            captureThreadName();
        }

        private void captureThreadName() {
            threadName = Thread.currentThread().getName();
        }

        public String getThreadName() {
            return threadName;
        }

        @Override
        public void accept(Object o) {
            captureThreadName();
        }

        @Override
        public void run() {
            captureThreadName();
        }

        public <T> Answer<T> answer(T value) {
            return x -> {
                captureThreadName();
                return value;
            };
        }
    }
}
