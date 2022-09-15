package victor.training.performance.spring.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.core.task.TaskDecorator;

import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;

public class MonitorQueueWaitingTimeTaskDecorator implements TaskDecorator {
    private final Timer timer;

    public MonitorQueueWaitingTimeTaskDecorator(Timer timer) {
        this.timer = timer;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        long t0 = currentTimeMillis();
        return () -> {
            long t1 = currentTimeMillis();

            timer.record(t1 - t0, TimeUnit.MILLISECONDS);
            runnable.run();
        };
    }
}
