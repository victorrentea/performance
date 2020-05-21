package victor.training.performance.batch.paritem;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class MyEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String value;
    public MyEntity(String value) {
        this.value = value;
    }
    public MyEntity() {}
}
