package victor.training.performance.java8.cf;

import org.checkerframework.checker.units.qual.A;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class TestUtils {

    static class CaptureThreadName implements BiConsumer<Object,Object> {

        private String threadName;
        @Override
        public void accept(Object o, Object o2) {
            threadName = Thread.currentThread().getName();
        }

        public String getThreadName() {
            return threadName;
        }
    }
}
