package victor.training.performance.batch.basic;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class PersonConverter implements ItemProcessor<PersonXml, Person> {
   @Autowired
   private CityRepo cityRepo;
   private Map<String, Long> cities;
   @Override
   public Person process(PersonXml xmlItem) throws Exception {
      Person entity = new Person();
      entity.setName(xmlItem.getName());

      if (cities == null) {
         cities = cityRepo.findAll().stream().collect(toMap(City::getName, City::getId));
      }

      Long cityId = cities.get(xmlItem.getCity());

      City city;
      if (cityId == null) {
         // de 10 ori in total
         city = new City();
         city.setName(xmlItem.getCity());
         cityRepo.save(city);
         cities.put(city.getName(), city.getId());
      } else {
         // 9990 ori
         city = cityRepo.findById(cityId).get();
      }

      entity.setCity(city);
      return entity;
   }
}
