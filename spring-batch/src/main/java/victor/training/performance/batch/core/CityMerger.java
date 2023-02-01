package victor.training.performance.batch.core;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.performance.batch.core.domain.City;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.stream.Collectors;

public class CityMerger implements ItemProcessor<PersonXml, City> {
   @Autowired
   private CityRepo repo;

   private Set<String> allCitiesInDB;

   @PostConstruct
   public void loadPreExistingCities() {
      allCitiesInDB = repo.findAll().stream().map(City::getName).collect(Collectors.toSet());
   }

   @Override
   public City process(PersonXml xml) {
      if (!allCitiesInDB.contains(xml.getCity())) {
         allCitiesInDB.add(xml.getCity());
         return new City(xml.getCity());
      } else {
         return null; // skip item
      }
   }
}
