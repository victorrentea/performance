package victor.training.jpa.perf;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Embeddable
public class Phone {
    private String value;
    protected Phone() {}

    public Phone(String value) {
        this.value = value;
    }

}
