package victor.training.performance.batch.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
public class MyEntityProcessor implements ItemProcessor<MyEntityFileRecord, MyEntity> {
    @Autowired
    private EntityManager em;

    @Override
    public MyEntity process(MyEntityFileRecord record) throws Exception {
//        log.debug("Proceesing item: " + item);
        MyEntity entity = new MyEntity();
        entity.setName(record.getName());
        List<City> citiesInDb = em.createQuery("SELECT c FROM City c WHERE c.name=:name", City.class)
            .setParameter("name", record.getCity())
            .getResultList();
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
