package victor.training.performance.batch.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MyEntityProcessor implements ItemProcessor<MyEntityFileRecord, MyEntity> {
   @Autowired
   private EntityManager em;

    private Map<String, Long> cityNameToId = new HashMap<>();

    // TODO >>> prelad in a @PostConstruct the existing cities before the batch

   @Override
   public MyEntity process(MyEntityFileRecord record) throws Exception {
//        log.debug("Proceesing item: " + item);
      MyEntity entity = new MyEntity();
      entity.setName(record.getName());

      Long cityId = cityNameToId.get(record.getCity());
      City city;
      if (cityId != null) {
         city = em.getReference(City.class, cityId);// NOT DO a select. Just give you a proxy to a chat will result in aFK being filled
      } else {
         city = new City(record.getCity());
         em.persist(city);
          cityNameToId.put(city.getName(), city.getId());
      }
      entity.setCity(city);
      return entity;
   }

//   // TODO optimize: map + em.getReference
//   private City resolveCity(MyEntityFileRecord record, List<City> citiesInDb) {
//      if (citiesInDb.isEmpty()) {
//         City city = new City(record.getCity());
//         em.persist(city);
//         return city;
//      } else if (citiesInDb.size() == 1) {
//         return citiesInDb.get(0);
//      } else {
//         throw new IllegalStateException("Duplicate country found in DB: " + record.getCity());
//      }
//   }
}

