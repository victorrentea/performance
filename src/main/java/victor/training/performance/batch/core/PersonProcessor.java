package victor.training.performance.batch.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import victor.training.performance.batch.core.domain.City;
import victor.training.performance.batch.core.domain.Person;

@Slf4j
@RequiredArgsConstructor
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
    private final CityRepo cityRepo;

    @Override
    public Person process(PersonXml xml) {
        Person entity = new Person();
        entity.setName(xml.getName());
        City city = cityRepo.findByName(xml.getCity())
            .orElseGet(() -> cityRepo.save(new City(xml.getCity())));

        entity.setCity(city);
        return entity;
    }

}
