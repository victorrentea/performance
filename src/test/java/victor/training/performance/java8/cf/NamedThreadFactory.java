package victor.training.performance.java8.cf;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
   private final  AtomicInteger i = new AtomicInteger();
    private String threadNamePrefix;

    public NamedThreadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread  t = new Thread(r);
        t.setName(threadNamePrefix + "-" + i.incrementAndGet());
        return t;
    }
}
