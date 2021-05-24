package victor.training.performance.batch.paritem;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.batch.item.ItemReader;

import static victor.training.performance.PerformanceUtil.sleepq;

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
        sleepq(1);
        return s;
    }
}
