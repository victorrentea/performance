package victor.training.performance.batch.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class PersonProcessor implements ItemProcessor<PersonXml, Person> {
    @Autowired
    private CityRepo cityRepo;

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
