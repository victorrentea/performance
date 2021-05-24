package victor.training.performance.batch.sync;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class City {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    public City(String name) {
        this.name = name;
    }
    public City() {}
}
