package victor.training.jpa.perf;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PARENT_WITH_LAST_CHILD")
public class ParentWithLastChild {
   @Id
   private Long id;
   private String name;
   private String childName;

   public void setName(String name) {
      this.name = name;
   }

   public void setChildName(String childName) {
      this.childName = childName;
   }

   public String getName() {
      return name;
   }

   public String getChildName() {
      return childName;
   }
}
