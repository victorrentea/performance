package victor.training.spring.batch.core.domain;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UK_CITY_NAME",columnNames = {"name"}))
//@Cache(usage = READ_ONLY)
public class City {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;

    public City(String name) {
        this.name = name;
    }
    public City() {}
}
