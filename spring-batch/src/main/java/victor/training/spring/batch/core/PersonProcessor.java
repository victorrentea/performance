package victor.training.spring.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.spring.batch.core.domain.City;
import victor.training.spring.batch.core.domain.CityRepo;
import victor.training.spring.batch.core.domain.Person;

@Slf4j
// 1 instance per job run
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
  @Autowired
  private CityRepo cityRepo;

  @Override
  public Person process(PersonXml xml) {
    Person entity = new Person();
    entity.setName(xml.getName());

    // insert if not there
    City city = cityRepo.findByName(xml.getCity())
        .orElseGet(() -> cityRepo.save(new City(xml.getCity())));

    entity.setCity(city);
    return entity;
  }

}
