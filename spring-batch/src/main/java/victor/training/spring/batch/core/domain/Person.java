package victor.training.spring.batch.core.domain;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@SequenceGenerator(name="person_seq", allocationSize = 50) // + INCREMENT BY in seq = 100
public class Person {
    @Id
    @GeneratedValue(generator = "person_seq", strategy = SEQUENCE)
    private Long id;
    private String name;
    @ManyToOne
    private City city;
    public Person(String name) {
        this.name = name;
    }
    public Person() {}
}
