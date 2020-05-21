package victor.training.jpa.perf;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@Entity
public class Address {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    private City city;
}
