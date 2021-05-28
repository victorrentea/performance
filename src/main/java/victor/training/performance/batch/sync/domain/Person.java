package victor.training.performance.batch.sync.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@SequenceGenerator(name = "person_seq")
public class Person {
    @Id
    @GeneratedValue(generator = "person_seq")
    private Long id;
    private String name;
    @ManyToOne
    private City city;
    public Person(String name) {
        this.name = name;
    }
    public Person() {}
}
