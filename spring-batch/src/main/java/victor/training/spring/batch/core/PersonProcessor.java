package victor.training.spring.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.spring.batch.core.domain.City;
import victor.training.spring.batch.core.domain.CityRepo;
import victor.training.spring.batch.core.domain.Person;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
    @Autowired
    private CityRepo cityRepo;

    Map<String, Long> cityNameToId = Collections.synchronizedMap(new HashMap<>());

    @PostConstruct
    public void atStartup() {
        cityNameToId = cityRepo.findAll()
            .stream().collect(Collectors.toMap(City::getName, City::getId));
    }

    @Override
    public Person process(PersonXml xml) {
        Person entity = new Person();
        entity.setName(xml.getName());

        Long cityId = cityNameToId.get(xml.getCity());
        City city;
        if (cityId != null) {
//            city = cityRepo.getReferenceById(cityId); // iti da un PROXY la ceva ce tu stii ca e sigur in DB
            city = new City().setId(cityId); // merge si asta
        } else {
            city = cityRepo.save(new City(xml.getCity()));
            cityNameToId.put(city.getName(), city.getId());
        }

        // TODO pt cine mai poate:
        // 1) design e prost: insera doua chestii odata Person, City.
        //    facem 2 treceri prin fisier:
        //     i) inserez City
        //     i) inserez Person
        // 2) multithreading OMG da, dar dupa 1) pt ca Person cu Person sa nu depinda

        entity.setCity(city);
        return entity;
    }

}
