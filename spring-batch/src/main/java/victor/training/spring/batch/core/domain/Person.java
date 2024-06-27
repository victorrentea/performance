package victor.training.spring.batch.core.domain;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@SequenceGenerator(name = "person_seq", allocationSize = 100)
public class Person {
    @Id
//    @GeneratedValue // daca doar atat lasi, by default PG/ORA iti iau din secventa
    // cate 1 id odata. Daca vrei sa iei mai multe, poti sa pui @SequenceGenerator

    @GeneratedValue(strategy = SEQUENCE, generator = "person_seq")
    private Long id;
    private String name;
    @ManyToOne
    private City city;
    public Person(String name) {
        this.name = name;
    }
    public Person() {}
}
