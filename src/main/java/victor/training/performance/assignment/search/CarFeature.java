package victor.training.performance.assignment.search;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class CarFeature {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    public CarFeature() {}
    public CarFeature(String name) {
        this.name = name;
    }
}
