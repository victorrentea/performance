package victor.training.jpa.perf;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class City {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
//    @OneToMany
//    private Set<Address> addresses = new HashSet<>();
}
