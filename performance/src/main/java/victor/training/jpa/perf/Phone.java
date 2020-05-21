package victor.training.jpa.perf;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Phone {
    @Id
    @GeneratedValue
    private Long id;
    private String value;
    protected Phone() {}

    public Phone(String value) {
        this.value = value;
    }

}
