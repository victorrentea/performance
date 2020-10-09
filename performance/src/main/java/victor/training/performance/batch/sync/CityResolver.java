package victor.training.performance.batch.sync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

@Component
public class CityResolver {
   @Autowired
   private EntityManager em;

   public City resolveCity(String cityName) {
      List<City> citiesInDb = em.createQuery("SELECT c FROM City c WHERE c.name=:name", City.class)
          .setParameter("name", cityName).getResultList();
      // TODO optimize: map + em.getReference
      if (citiesInDb.isEmpty()) {
         City city = new City(cityName);
         em.persist(city);
         return city;
      } else if (citiesInDb.size() == 1) {
         return citiesInDb.get(0);
      } else {
         throw new IllegalStateException("Duplicate country found in DB: " + cityName);
      }
   }

}
