package victor.training.performance.batch.sync;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@Entity
@XmlRootElement(name = "data")
public class MyEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String value;
    public MyEntity(String value) {
        this.value = value;
    }
    public MyEntity() {}
}
