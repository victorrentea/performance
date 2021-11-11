package victor.training.performance.batch.core.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UK_CITY_NAME",columnNames = {"name"}))
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
