package victor.training.spring.batch.assignment;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@Entity
public class Quotation {
    @Id
    @GeneratedValue
    private Long id;
    private String customerName;
    @ManyToOne
    private Tag tag;

    public Quotation(String customerName) {
        this.customerName = customerName;
    }
    public Quotation() {}
}
