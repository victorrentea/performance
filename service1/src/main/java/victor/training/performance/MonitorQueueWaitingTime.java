package victor.training.performance;

import io.micrometer.core.instrument.Timer;
import org.springframework.core.task.TaskDecorator;

import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;

public class MonitorQueueWaitingTime implements TaskDecorator {
    private final Timer timer;

    public MonitorQueueWaitingTime(Timer timer) {
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
