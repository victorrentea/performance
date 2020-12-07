package victor.training.performance.pools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import victor.training.performance.ConcurrencyUtil;

@SpringBootApplication
@Slf4j
public class ScheduledApp  {
    public static void main(String[] args) {
        SpringApplication.run(ScheduledApp.class);
    }

    // TODO 1 Should run every 5 seconds / configurable / cron "*/5 * * * * *"
    // TODO 3 Play with delays. cron vs fixedRate? Overlapping executions?
    // TODO 4 Should run on a separate 1-thread pool
    public void lookIntoFolder() {
        log.debug("Looking into folder");
        ConcurrencyUtil.sleepq(7000);
        log.debug("DONE");
    }

    // TODO 2 define another task at each second. This should not block.
    // TODO explore application.properties (thread #)
    public void pollFast() {
        log.debug("FAST each second");
    }
}