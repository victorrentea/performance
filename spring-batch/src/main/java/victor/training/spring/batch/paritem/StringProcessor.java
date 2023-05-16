package victor.training.spring.batch.paritem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import victor.training.spring.batch.util.PerformanceUtil;

@Slf4j
public class StringProcessor implements ItemProcessor<String, String> {
    @Override
    public synchronized String process(String item) {
        log.debug("Start processing " + item);
        PerformanceUtil.sleepMillis(100);
        log.debug("End processing " + item);
        return item.toUpperCase();
    }
}
