package victor.training.spring.batch.assignment;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;


@Data
@XmlRootElement(name = "quotation")
public class QuotationRecord {
    private String name;
    private String city;
}
