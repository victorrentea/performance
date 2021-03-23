package victor.training.performance.leaks;

import lombok.Data;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
public class OrderController {
}


@Entity
@Data
class Product {
   @Id
   @GeneratedValue
   private Long id;
   private String name;
   private String description;
   private Long supplierId;
   private LocalDate createDate;
}
