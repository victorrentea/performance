package victor.training.performance.batch.sync;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@Entity
public class MyEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    private City city;
    public MyEntity(String name) {
        this.name = name;
    }
    public MyEntity() {}
}
