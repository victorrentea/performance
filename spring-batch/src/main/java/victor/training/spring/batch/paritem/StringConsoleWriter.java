package victor.training.spring.batch.paritem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import victor.training.spring.batch.util.PerformanceUtil;

import java.util.List;

@Slf4j
public class StringConsoleWriter implements ItemWriter<String> {
    // TODO ItemStreamWriter
    @Override
    public void write(List<? extends String> list) {
        PerformanceUtil.sleepMillis(1);
        for (String s : list) {
            log.debug("Write " + s);
        }
    }
}
