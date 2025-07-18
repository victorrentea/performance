package victor.training.spring.batch.core;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.spring.batch.core.domain.City;
import victor.training.spring.batch.core.domain.CityRepo;
import victor.training.spring.batch.core.domain.Person;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
// 1 instance per job run
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
  @Autowired
  private CityRepo cityRepo;
  @Autowired
  private EntityManager em;

  Map<String, Long> cityNamesInDB = new HashMap<>();

  @PostConstruct
  void init() {
    cityNamesInDB = cityRepo.streamAll()
        .peek(em::detach) // JPA, nu tine minte acesta entitate in 1st level cache
        .collect(toMap(City::getName, City::getId));
    // TODO @george un @Query care sa scoata doar City: String name si Long id
  }

  @Override
  public Person process(PersonXml xml) {
    Person entity = new Person();
    entity.setName(xml.getName());

    Long cityId = cityNamesInDB.get(xml.getCity());
//    City city = cityRepo.findById(cityId).orElseThrow();
    City city = cityRepo.getReferenceById(cityId);// NU FACE SELECT
    // ci iti da un proxy la @Entity pe care
    // pe care poti s-o pui ca FK al unei alte @Entity

    entity.setCity(city);
    return entity;
  }

}
