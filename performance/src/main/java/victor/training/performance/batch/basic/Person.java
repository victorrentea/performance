package victor.training.performance.batch.basic;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
