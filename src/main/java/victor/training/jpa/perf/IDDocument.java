package victor.training.jpa.perf;

import javax.persistence.*;

@Entity
public class IDDocument {

    @Id
    @GeneratedValue
    private Long id;
}
