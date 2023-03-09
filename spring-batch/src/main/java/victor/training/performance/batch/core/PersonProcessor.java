package victor.training.performance.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.performance.batch.core.domain.City;
import victor.training.performance.batch.core.domain.CityRepo;
import victor.training.performance.batch.core.domain.Person;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
    @Autowired
    private CityRepo cityRepo;
    
    private Map<String, Long> cityIdByName;
    @PostConstruct
    public void method() {
        log.info("Loading cities from DB (merged in pass #1)...");
        cityIdByName = cityRepo.findAll().stream().collect(Collectors.toMap(City::getName, City::getId));
    }

    @Override
    public Person process(PersonXml xml) {
        Person entity = new Person();
        entity.setName(xml.getName());
//        City city = cityRepo.findByName(xml.getCity())
//            .orElseGet(() -> cityRepo.save(new City(xml.getCity())));

        City city = cityRepo.getReferenceById(cityIdByName.get(xml.getCity()));

        entity.setCity(city);
        return entity;
    }

}
