package victor.training.spring.batch.paritem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import victor.training.spring.batch.util.PerformanceUtil;

import java.util.List;

@Slf4j
public class StringConsoleWriter implements ItemWriter<String> {
    // TODO ItemStreamWriter
    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        PerformanceUtil.sleepMillis(1);
        for (String s : chunk.getItems()) {
            log.debug("Write " + s);
        }
    }
}
