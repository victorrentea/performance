package victor.training.spring.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.spring.batch.core.domain.City;
import victor.training.spring.batch.core.domain.CityRepo;
import victor.training.spring.batch.core.domain.Person;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
  @Autowired
  private CityRepo cityRepo;
  private Map<String, Long> cityNameToId;

  @PostConstruct
  public void loadInitialCities() {
    cityNameToId = cityRepo.findAll().stream()
        .collect(Collectors.toMap(City::getName, City::getId));
  }

  @Override
  public Person process(PersonXml xml) {
    Person entity = new Person();
    entity.setName(xml.getName());
    City city;
    if (cityNameToId.containsKey(xml.getCity())) {
      Long cityId = cityNameToId.get(xml.getCity());
      city = cityRepo.getReferenceById(cityId);
    } else {
      city = new City(xml.getCity());
      cityRepo.save(city);
      cityNameToId.put(city.getName(), city.getId());
    }
    entity.setCity(city);
    return entity;
  }

}
