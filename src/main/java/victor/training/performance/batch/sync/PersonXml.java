package victor.training.performance.batch.sync;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement(name = "person")
public class PersonXml {
    private String name;
    private String city;

    public PersonXml(Person person) {
        this.city = person.getCity().getName();
        this.name = person.getName();
    }
}
