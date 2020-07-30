package victor.training.performance.batch.basic;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@SequenceGenerator(name = "seqgen", allocationSize = 50)
public class Person {
    @Id
    @GeneratedValue(generator = "seqgen")
    private Long id;
    private String name;
    @ManyToOne
    private City city;

    public Person(String name) {
        this.name = name;
    }
    public Person() {}
}
