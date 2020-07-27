package victor.training.performance.batch.paritem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

import static victor.training.performance.ConcurrencyUtil.sleepq;

@Slf4j
public class StringConsoleWriter implements ItemWriter<String> {
    // TODO ItemStreamWriter
    @Override
    public void write(List<? extends String> list) {
        sleepq(1);
        for (String s : list) {
            log.debug("Write " + s);
        }
    }
}
