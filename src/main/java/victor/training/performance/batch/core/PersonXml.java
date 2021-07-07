package victor.training.performance.batch.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import victor.training.performance.batch.core.domain.Person;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement(name = "person")
public class PersonXml {
    private String name;
    private String city;

    public PersonXml(Person person) {
        name = person.getName();
        city = person.getCity().getName();
    }
}
