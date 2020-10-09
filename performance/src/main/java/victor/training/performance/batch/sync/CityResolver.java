package victor.training.performance.batch.sync;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@JobScope
public class CityResolver {

   @Autowired
   private CityRepo cityRepo;
   private Map<String, Long> cityIdByName = new HashMap<>();

   @PostConstruct
   public void loadPreExistingCities() {
      cityIdByName=cityRepo.findAll().stream().collect(Collectors.toMap(City::getName, City::getId));
   }

   public City resolveCity(String cityName) {
      if (cityIdByName.containsKey(cityName)) {
         Long cityId = cityIdByName.get(cityName);
         return cityRepo.getOne(cityId); // NU FACE SELECT
      } else {
         City city = new City(cityName);
         cityRepo.save(city);
         cityIdByName.put(cityName, city.getId());
         return city;
      }

//      List<City> citiesInDb = em.createQuery("SELECT c FROM City c WHERE c.name=:name", City.class)
//          .setParameter("name", cityName).getResultList();
//      // TODO optimize: map + em.getReference
//      if (citiesInDb.isEmpty()) {
//         return city;
//      } else if (citiesInDb.size() == 1) {
//         return citiesInDb.get(0);
//      } else {
//         throw new IllegalStateException("Duplicate country found in DB: " + cityName);
//      }
   }

}
