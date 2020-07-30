package victor.training.performance.batch.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class PersonConverter implements ItemProcessor<PersonXml, Person> {
   private static final Logger log = LoggerFactory.getLogger(PersonConverter.class);
   @Autowired
   private CityRepo cityRepo;


   @Value("#{jobExecutionContext['file.name.param']}")
   private String fileName;
   @Autowired
   private CityResolver cityResolver;

   @Override
   public Person process(PersonXml xmlItem) throws Exception {
      Person entity = new Person();
      entity.setName(xmlItem.getName());

      Map<String, Long> cities = cityResolver.resolveAll();


      Long cityId = cities.get(xmlItem.getCity());

      if (cityId == null) {
         // de 10 ori in total
         City city = new City();
         city.setName(xmlItem.getCity());
         cityRepo.save(city);
         cities.put(city.getName(), city.getId());
         cityId = city.getId();
      }
      entity.setCityId(cityId);
      return entity;
   }
}

@Component
class CityResolver {
   @Autowired
   private CityRepo cityRepo;

   @Cacheable("cities")
   public Map<String, Long> resolveAll() {
      return cityRepo.findAll().stream().collect(toMap(City::getName, City::getId));
   }
}
