package victor.training.performance.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.performance.batch.core.domain.City;
import victor.training.performance.batch.core.domain.Person;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
    @Autowired
    private CityRepo cityRepo;


    Map<String, Long> cityIdByName;

    @PostConstruct
    public void loadPreExistingCities() {
        cityIdByName = cityRepo.findAll().stream()
                .collect(Collectors.toMap(City::getName, City::getId));
    }

    @Override
    public Person process(PersonXml xml) {
        Person person = new Person();
        person.setName(xml.getName());

//        City city = cityRepo.findByName(xml.getCity())
//            .orElseGet(() -> cityRepo.save(new City(xml.getCity())));
        City city;
        Long cityId = cityIdByName.get(xml.getCity());
        if (cityId != null) {
//            city = cityRepo.findById(cityId).orElseThrow(); // wrong + 1 query
            city = cityRepo.getReferenceById(cityId);
        } else {
            city = cityRepo.save(new City(xml.getCity()));
            cityIdByName.put(xml.getCity(), city.getId());
        }

        person.setCity(city);
        return person;
    }

}
