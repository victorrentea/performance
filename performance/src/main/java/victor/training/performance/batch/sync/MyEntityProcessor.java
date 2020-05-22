package victor.training.performance.batch.sync;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class MyEntityProcessor implements ItemProcessor<MyEntity, MyEntity> {
    @Override
    public MyEntity process(MyEntity item) throws Exception {
        log.debug("Proceesing item: " + item);
        return item;
    }
}
