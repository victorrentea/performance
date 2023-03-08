package victor.training.performance.batch.core.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@SequenceGenerator(name = "myseqgen")
public class Person {
    @Id
//    @GeneratedValue // picks one ID at a time from the seq
    @GeneratedValue(generator = "myseqgen")
    private Long id;
    private String name;
    @ManyToOne
    private City city;
    public Person(String name) {
        this.name = name;
    }
    public Person() {}
}
