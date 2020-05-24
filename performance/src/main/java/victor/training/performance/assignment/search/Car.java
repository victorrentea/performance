package victor.training.performance.assignment.search;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Car {
    @Id
    @GeneratedValue
    private Long id;
    private String make;
    private Integer year;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<CarFeature> features = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<CarOwner> owners = new HashSet<>();
}
