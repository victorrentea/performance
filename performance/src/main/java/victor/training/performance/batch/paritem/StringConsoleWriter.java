package victor.training.performance.batch.paritem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

import static victor.training.performance.ConcurrencyUtil.sleep2;

@Slf4j
public class StringConsoleWriter implements ItemWriter<String> {
    @Override
    public void write(List<? extends String> list) {
        sleep2(1);
        for (String s : list) {
            log.debug("Write " + s);
        }
    }
}
