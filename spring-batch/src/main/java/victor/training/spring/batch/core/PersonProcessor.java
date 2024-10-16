package victor.training.spring.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.spring.batch.core.domain.City;
import victor.training.spring.batch.core.domain.CityRepo;
import victor.training.spring.batch.core.domain.Person;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
  @Autowired
  private CityRepo cityRepo;
  private Map<String, City> cityNameToId;

  @PostConstruct
  public void loadInitialCities() {
    cityNameToId = cityRepo.findAll().stream()
        .collect(Collectors.toMap(City::getName, Function.identity()));
  }

  @Override
  public Person process(PersonXml xml) {
    Person entity = new Person();
    entity.setName(xml.getName());
    entity.setCity(cityNameToId.get(xml.getCity()));
    return entity;
  }

}
