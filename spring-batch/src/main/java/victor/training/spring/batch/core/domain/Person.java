package victor.training.spring.batch.core.domain;

import lombok.Data;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity
public class Person {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    private City city;
    public Person(String name) {
        this.name = name;
    }
    public Person() {}
}
