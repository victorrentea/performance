package victor.training.performance.batch.sync;

import org.springframework.batch.item.ItemProcessor;
import victor.training.performance.batch.sync.domain.City;

import java.util.HashSet;
import java.util.Set;

public class CityProcessor implements ItemProcessor<PersonXml, City> {

   Set<String> cityNames = new HashSet<>();

   @Override
   public City process(PersonXml item) throws Exception {
      if (cityNames.contains(item.getCity())) {
         return null;
      }
      cityNames.add(item.getCity());
      return new City(item.getCity());
   }
}
