package victor.training.spring.batch.core.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
//@TableGenerator()
@SequenceGenerator(name = "PersonSeq")
public class Person {
    @Id
    @GeneratedValue(generator = "PersonSeq")
    private Long id;
    private String name;
    @ManyToOne
    private City city;
    public Person(String name) {
        this.name = name;
    }
    public Person() {}
}
