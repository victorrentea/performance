package victor.training.spring.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.spring.batch.core.domain.City;
import victor.training.spring.batch.core.domain.CityRepo;
import victor.training.spring.batch.core.domain.Person;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
    @Autowired
    private CityRepo cityRepo;
    @Autowired
    private EntityManager entityManager;

    //    private final Set<UUID>
    private final Map<String, Long> cityNameToId = new HashMap<>();

    @Override
    public Person process(PersonXml xml) {
        Person entity = new Person();
        entity.setName(xml.getName());

        if (cityNameToId.containsKey(xml.getCity())) {
            // nu face DB query ci te crede pe cuvant ca exista City cu id dat
//            City cityProxy = entityManager.getReference(City.class, cityNameToId.get(xml.getCity()));

            City city = new City();
            city.setId(cityNameToId.get(xml.getCity()));
            entity.setCity(city);
        } else {
            City city = cityRepo.save(new City(xml.getCity()));
            entity.setCity(city);
            cityNameToId.put(city.getName(), city.getId());
        }

        return entity;
    }

}
