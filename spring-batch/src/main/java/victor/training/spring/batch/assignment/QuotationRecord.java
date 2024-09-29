package victor.training.spring.batch.assignment;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "quotation")
public class QuotationRecord {
    private String name;
    private String city;
}
