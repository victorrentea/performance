package victor.training.performance.batch.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MyEntityProcessor implements ItemProcessor<MyEntityFileRecord, MyEntity> {
    @Autowired
    private EntityManager em;



    private List<City> allCitiesInDB;

    @Override
    public MyEntity process(MyEntityFileRecord record) throws Exception {
//        log.debug("Proceesing item: " + item);
        MyEntity entity = new MyEntity();
        entity.setName(record.getName());

        if (allCitiesInDB == null) {
            allCitiesInDB = em.createQuery("SELECT c FROM City c", City.class).getResultList();
        }

//        List<City> citiesInDb = em.createQuery("SELECT c FROM City c WHERE c.name=:name", City.class).setParameter("name", record.getCity()).getResultList();
        City city = resolveCity(record.getCity());
        entity.setCity(city);
        return entity;
    }

    // TODO optimize: map + em.getReference
    private City resolveCity(String cityName) {
        Optional<City> cityInDB = allCitiesInDB.stream()
            .filter(c -> c.getName().equals(cityName))
            .findFirst();

        if (cityInDB.isPresent()) {
            return em.getReference(City.class, cityInDB.get().getId());
        } else {
            City newCityToInsert = new City(cityName);
            em.persist(newCityToInsert);
            allCitiesInDB.add(newCityToInsert);
            return newCityToInsert;
        }
    }
}
