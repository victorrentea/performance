package victor.training.performance.batch.sync;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "data")
public class MyEntityFileRecord {
    private String name;
    private String city;
}
