package victor.training.spring.batch.assignment;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

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
