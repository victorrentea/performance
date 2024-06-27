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
//        City city = cityRepo.findByName(personXml.getCity())
//            .orElseGet(() -> cityRepo.save(new City(personXml.getCity())));
        Long cityId = cityIdByName.get(personXml.getCity());
        City city;
        if (cityId != null) {
            city = new City();
            city.setId(cityId);
        } else {
            city = new City();
            city.setName(personXml.getCity());
            city = cityRepo.save(city);
            cityIdByName.put(city.getName(), city.getId());
        }

        entity.setCity(city);
        return entity;
    }


    // trecerea 1 prin fisier insereaza doar orasele - 1 thread, ca-s sute
    // trecerea 2 prin fisier insereaza persoanele - 4 threaduri, ca-s mii/milioane
}
