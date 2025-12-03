package victor.training.spring.batch.core;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.spring.batch.core.domain.City;
import victor.training.spring.batch.core.domain.CityRepo;
import victor.training.spring.batch.core.domain.Person;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
// 1 instance per job run
  // => poti sa-ti lasi date in fielduri specifice rularii curente
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
  @Autowired
  private CityRepo cityRepo;

  // city.name -> city.id
  private Map<String, Long> citiesMap; // OK cat e single threaded

  @PostConstruct
  public void startup() {
    citiesMap = cityRepo.findAll().stream() // 1 SELECT
        .collect(toMap(City::getName,City::getId));
  }

  @Override
  public Person process(PersonXml xml) {
    Person entity = new Person();
    entity.setName(xml.getName());
    String cityName = xml.getCity();
//    City city = cityRepo.findByName(cityName)// DAMAGE = 1M x SELECT
//        .orElseGet(() -> cityRepo.save(new City(cityName)));

    Long cityId = citiesMap.get(cityName);
    if (cityId == null) {
      cityId = cityRepo.save(new City(cityName)).getId();
      citiesMap.put(cityName, cityId);
    }
    // nu vrei SELECT, ci un placeholder city care la save(person) -- doar sa seteze FK Person->City
    // = 0 SELECT❤️
//    City city = new City().setId(cityId); //1
    City city = cityRepo.getReferenceById(cityId);//2 mai safe

//    City city = cityRepo.findById(cityId).orElseThrow(); // 1 SELECT / oras / chunk
    //tot e ok ca ai 1 tx/chunk si te salveza 1st level cache Hibernate ca ai doar 5 orase in fisier

    entity.setCity(city);
    return entity;
  }

}
