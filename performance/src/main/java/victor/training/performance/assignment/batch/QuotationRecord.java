package victor.training.performance.assignment.batch;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "quotation")
public class QuotationRecord {
    private String name;
    private String city;
}
