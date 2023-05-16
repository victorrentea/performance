package victor.training.spring.batch.paritem;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.batch.item.ItemReader;
import victor.training.spring.batch.util.PerformanceUtil;

@Slf4j
public class StringGeneratorReader implements ItemReader<String> {
    // TODO try ItemStreamReader
    private int totalItemsToGenerate; // FIXME race condition

    public StringGeneratorReader(int totalItemsToGenerate) {
        this.totalItemsToGenerate = totalItemsToGenerate;
    }

    public String read() {
        if (totalItemsToGenerate <= 0) {
            return null;
        }
        totalItemsToGenerate--;
        String s = RandomStringUtils.randomAlphabetic(4);
        log.debug("Read " + s);
        PerformanceUtil.sleepMillis(1);
        return s;
    }
}
