package victor.training.performance.batch.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
public class MyEntityProcessor implements ItemProcessor<MyEntityFileRecord, MyEntity> {

    @Value("#{jobParameters['param1']}")
    private String param1;
    @Value("#{jobExecutionContext['MY_START_TIME']}")
    private String myStartTime;

    @Override
    public MyEntity process(MyEntityFileRecord record) throws Exception {
//        System.out.println("param1=  "+param1);
//        System.out.println("myStartTime=  "+myStartTime);
//        log.debug("Proceesing item: " + item);
//        if (true) {
//            throw new IllegalArgumentException();
//        }
        MyEntity entity = new MyEntity();
        entity.setName(record.getName());
        City city = cityResolver.resolveCity(record.getCity());
        entity.setCity(city);
        return entity;
    }
    @Autowired
    private CityResolver cityResolver;

}
