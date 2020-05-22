package victor.training.performance.batch.paritem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import static victor.training.performance.ConcurrencyUtil.sleep2;

@Slf4j
public class StringProcessor implements ItemProcessor<String, String> {
    @Override
    public synchronized String process(String item) {

        //TODO  simuleaza add in colectie partajata
        log.debug("Start processing " + item);
        sleep2(100);
        log.debug("End processing " + item);
        return item.toUpperCase();
    }
}
