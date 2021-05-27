package victor.training.performance.batch.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
    @Autowired
    private CityRepo cityRepo;

    private Map<String, Long> allCitiesInDB;

    @PostConstruct
    public void preloadCities() {
        allCitiesInDB = cityRepo.findAll().stream().collect(toMap(City::getName, City::getId));
        log.info("Loaded cities: " + allCitiesInDB);
    }

    @Override
    public Person process(PersonXml xml) {
        Person entity = new Person();
        entity.setName(xml.getName());
        Long id = allCitiesInDB.get(xml.getCity());
        City citiRef = cityRepo.getOne(id);
        entity.setCity(citiRef);
        return entity;
    }


}
