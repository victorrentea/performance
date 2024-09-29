package victor.training.performance.jpa.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Data
public class CountryRegion {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
