package victor.training.spring.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.spring.batch.core.domain.City;
import victor.training.spring.batch.core.domain.CityRepo;
import victor.training.spring.batch.core.domain.Person;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
    @Autowired
    private CityRepo cityRepo;

    private Map<String, Long> cityIdByName = new HashMap<>();

    @PostConstruct
    public void loadInitialCities() {
        cityIdByName = cityRepo.findAll().stream()
            .collect(Collectors.toMap(City::getName, City::getId));
    }

    @Override
    public Person process(PersonXml personXml) {
//        if (xml.getName().contains("elem16")) {
//            throw new IllegalArgumentException("EROARE"); // by default spring batch intrerupe executia
            // aruncand chunkul curent la gunoi,
            // dar putem sa configuram un RetryPolicy
            // sau un SkipPolicy

            // daca insa vrei sa ignori tu manual erorile si sa le raportezi intr-un fisier de ex
            // poti sa introrci null.
//        }

        Person entity = new Person();
        entity.setName(personXml.getName());
        City city = cityRepo.findByName(personXml.getCity())
            .orElseGet(() -> cityRepo.save(new City(personXml.getCity())));

        entity.setCity(city);
        return entity;
    }

}
