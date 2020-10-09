package victor.training.performance.batch.sync;

import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@Entity
@SequenceGenerator(name = "MyEntityGenerator", allocationSize = 50)
public class MyEntity {
    @Id
    @GeneratedValue(generator = "MyEntityGenerator")
    private Long id;
    private String name;
    @ManyToOne
    private City city;
    public MyEntity(String name) {
        this.name = name;
    }
    public MyEntity() {}
}
