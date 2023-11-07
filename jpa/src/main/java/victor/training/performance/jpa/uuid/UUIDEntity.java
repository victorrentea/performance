package victor.training.performance.jpa.uuid;

import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Getter
@Entity
@GenericGenerator(name = "uuid", strategy = "victor.training.performance.jpa.uuid.UUIDGenerator")
public class UUIDEntity {
   @Id
   // Option 1: because hibernate sees a NON-null PK, it issues a prior SELECT to find out if your repo.save is not an UPDATE
   // if no results => INSERT; else UPDATE
//   private String id = UUID.randomUUID().toString();

   // Option 2: Hibernate sees a NULL @Id and calls the custom generator at .save() ==> no SELECT
   @GeneratedValue(generator = "uuid")
   private String id;

   private String name;
}

