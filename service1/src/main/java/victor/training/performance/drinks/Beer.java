package victor.training.performance.drinks;

import lombok.Data;

@Data
public class Beer {
   private String type;

   public Beer setType(String type) {
      this.type = type;
      return this;
   }
}
