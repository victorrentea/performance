package victor.training.performance.batch.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import victor.training.performance.batch.core.domain.City;
import victor.training.performance.batch.core.domain.Person;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
    @Autowired
    private CityRepo cityRepo;

    @Override
    public Person process(PersonXml xml) {
        Person entity = new Person();
        entity.setName(xml.getName());
        City city = paraNada.resolveCityByName(xml.getCity());
        entity.setCity(city);
        return entity;
    }
    @Autowired
    private ParaNada paraNada;
}
@Slf4j
@RequiredArgsConstructor
@Service
class ParaNada {
    private final CityRepo cityRepo;
    @Cacheable("cities")
    public City resolveCityByName(String cityName) {
        return cityRepo.findByName(cityName)
            .orElseGet(() -> cityRepo.save(new City(cityName)));

    }
    // What about - get all, then filter(by id), find first - idk if this is faster
}
