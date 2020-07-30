package victor.training.performance.batch.basic;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonConverter implements ItemProcessor<PersonXml, Person> {
   @Autowired
   private CityRepo cityRepo;
   @Override
   public Person process(PersonXml item) throws Exception {
      Person entity = new Person();
      entity.setName(item.getName());

      City city = cityRepo.findByName(item.getCity());
      if (city == null) {
         city = new City();
         city.setName(item.getCity());
         cityRepo.save(city);
      }

      entity.setCity(city);
      return entity;
   }
}
