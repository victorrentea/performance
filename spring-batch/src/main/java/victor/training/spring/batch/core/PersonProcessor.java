package victor.training.spring.batch.core;

import com.google.common.collect.ImmutableMap;
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

import static com.google.common.collect.ImmutableMap.toImmutableMap;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
  @Autowired
  private CityRepo cityRepo;
  private ImmutableMap<String, City> cityNameToId;

  @PostConstruct
  public void loadInitialCities() { //1 data intr-un singur thread (main)
    cityNameToId = cityRepo.findAll().stream()
        .collect(toImmutableMap(City::getName, Function.identity()));
  }

  @Override
  public Person process(PersonXml xml) { // 8 threaduri aici
    Person entity = new Person();
    entity.setName(xml.getName());
    entity.setCity(cityNameToId.get(xml.getCity()));
    return entity;
  }

}
