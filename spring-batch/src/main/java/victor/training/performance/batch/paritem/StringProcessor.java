package victor.training.performance.batch.paritem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import static victor.training.performance.batch.PerformanceUtil.sleepMillis;

@Slf4j
public class StringProcessor implements ItemProcessor<String, String> {
    @Override
    public synchronized String process(String item) {
        log.debug("Start processing " + item);
        sleepMillis(100);
        log.debug("End processing " + item);
        return item.toUpperCase();
    }
}
