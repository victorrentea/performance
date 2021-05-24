package victor.training.performance.batch.sync;

import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@SequenceGenerator(name = "myseqgen", allocationSize = 50)
@Entity
public class MyEntity {
    @Id
    @GeneratedValue(generator = "myseqgen")
    private Long id;
    private String name;
    @ManyToOne
    private City city;
    public MyEntity(String name) {
        this.name = name;
    }
    public MyEntity() {}
}
