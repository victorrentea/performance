package victor.training.performance.batch.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
    @Autowired
    private CityRepo cityRepo;

    private Map<String, Long> citiesByName = new HashMap<>();
    
    @PostConstruct
    public void loadExistingCities() {
        citiesByName = cityRepo.findAll().stream().collect(Collectors.toMap(City::getName,City::getId));
    }

    @Override
    public Person process(PersonXml xml) {
        Person entity = new Person();
        entity.setName(xml.getName());

        Long cityId = citiesByName.get(xml.getCity());


        if (cityId == null) {
            City newCity = cityRepo.save(new City(xml.getCity()));
            cityId = newCity.getId();
            citiesByName.put(newCity.getName(), cityId);
        }

        City city = cityRepo.getOne(cityId);
        entity.setCity(city);
        return entity;
    }

}
