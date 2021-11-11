package victor.training.performance.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.performance.batch.core.domain.City;
import victor.training.performance.batch.core.domain.Person;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
    @Autowired
    private CityRepo cityRepo;

    private Map<String, City> cityCache = new HashMap<>();

    @Override
    public Person process(PersonXml xml) {
        Person entity = new Person();
        entity.setName(xml.getName());

        if (cityCache.containsKey(xml.getCity())) {
            entity.setCity(cityCache.get(xml.getCity()));
        } else {
            City city = cityRepo.save(new City(xml.getCity()));
            cityCache.put(xml.getCity(), city);
            entity.setCity(city); // NU MERGE pentru ca City entitatea este NECUNOSCUTA tranzactiei ACUM ACTIVE
        }

//        City city = cityRepo.findByName(xml.getCity())
//            .orElseGet(() -> cityRepo.save(new City(xml.getCity())));

//        entity.setCity(city);
        return entity;
    }

    // cum putem face sa nu mergem dupa oras PT FIECARE RECORD.
    // stim ca distrib datelor e 1000 person / oras..

    // doar cand vad un oras NOU sa-l caut in baza
    // >> trebuie sa


}
