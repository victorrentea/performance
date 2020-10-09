package victor.training.performance.batch.sync;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
public class MyEntityProcessor implements ItemProcessor<MyEntityFileRecord, MyEntity> {
    @Autowired
    private EntityManager em;


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
        List<City> citiesInDb = em.createQuery("SELECT c FROM City c WHERE c.name=:name", City.class)
            .setParameter("name", record.getCity()).getResultList();
        City city = resolveCity(record, citiesInDb);
        entity.setCity(city);
        return entity;
    }

    // TODO optimize: map + em.getReference
    private City resolveCity(MyEntityFileRecord record, List<City> citiesInDb) {
        if (citiesInDb.isEmpty()) {
            City city = new City(record.getCity());
            em.persist(city);
            return city;
        } else if (citiesInDb.size() == 1) {
            return citiesInDb.get(0);
        } else {
            throw new IllegalStateException("Duplicate country found in DB: " + record.getCity());
        }
    }
}
