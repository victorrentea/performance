package victor.training.spring.batch.assignment;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Data
@Entity
public class Tag {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    public Tag(String name) {
        this.name = name;
    }
    public Tag() {}
}
