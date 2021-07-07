package victor.training.performance.jpa;

import javax.persistence.*;

@Entity
public class IDDocument {

    @Id
    @GeneratedValue
    private Long id;
}
