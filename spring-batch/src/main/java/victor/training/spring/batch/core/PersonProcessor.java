package victor.training.spring.batch.core;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.spring.batch.core.domain.City;
import victor.training.spring.batch.core.domain.CityRepo;
import victor.training.spring.batch.core.domain.Person;

import java.util.Collections;
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

  @PostConstruct
  void init() {
    cityNamesInDB = cityRepo.streamAll()
        .peek(em::detach) // JPA, nu tine minte acesta entitate in 1st level cache
        .collect(toMap(City::getName, City::getId));
    // TODO @george un @Query care sa scoata doar City: String name si Long id
  }


  Map<String, Long> cityNamesInDB = Collections.synchronizedMap(new HashMap<>());

  @Override
  public  Person process(PersonXml xml) {
    Person entity = new Person();
    entity.setName(xml.getName());

    City city;
    if (!cityNamesInDB.containsKey(xml.getCity())) {
      city = cityRepo.save(new City(xml.getCity()));
      cityNamesInDB.put(xml.getCity(), city.getId());
    } else {
      Long cityId = cityNamesInDB.get(xml.getCity());
      city = cityRepo.findById(cityId).orElseThrow();
      // explicatie: 1st level cache al lui Hibernate
      // fiecare chunk se proceseaza in aceeasi tranzactie
      // JPA in cadrul aceleasi tranzactii, cand faci
      // var e = repo.findById(42L); => SELECt
      // var e = repo.findById(42L); => JPA ti-l serveste din mem
      // iti da al doilea rez din memorie
    }

    entity.setCity(city);
    return entity;
  }

}
