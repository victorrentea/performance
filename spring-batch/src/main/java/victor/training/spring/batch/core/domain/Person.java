package victor.training.spring.batch.core.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@SequenceGenerator(name = "personSeq", allocationSize = 50)
// problema de UX: lipsesc IDuri: 1,2,3,4...50,51,52,<GAURA> 101,102,103
public class Person {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)//generator = "personSeq")
    @GeneratedValue(generator = "personSeq")
    private Long id;
    private String name;
    @ManyToOne
    private City city;
    public Person(String name) {
        this.name = name;
    }
    public Person() {}
}
