package victor.training.performance.batch.sync;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@Entity
@XmlRootElement(name = "data") // in viata reala nu as recomanda sa
// demarshallui cu JAXB direct in@Entity
@SequenceGenerator(name = "MY_SEQ", allocationSize = 20)
public class MyEntity {
    @Id
    @GeneratedValue(generator = "MY_SEQ")
    private Long id;
    private String value;
    public MyEntity(String value) {
        this.value = value;
    }
    public MyEntity() {}
}
