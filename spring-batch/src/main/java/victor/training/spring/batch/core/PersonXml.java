package victor.training.spring.batch.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import victor.training.spring.batch.core.domain.Person;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement(name = "person")
// mapped to the input XML structure using JAXB
public class PersonXml {
    private String name;
    private String city;

    public PersonXml(Person person) {
        name = person.getName();
        city = person.getCity().getName();
    }
}
