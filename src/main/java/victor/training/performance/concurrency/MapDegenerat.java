package victor.training.performance.concurrency;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

public class MapDegenerat {


   public static void main(String[] args) {
      Map<ElementulNostru, String> nuPreaFaci = new HashMap<>();
      Set<ElementulNostru> puiiMei = new HashSet<>();

      ElementulNostru childPoc = new ElementulNostru("Emma-Simona");

      puiiMei.add(childPoc);

      System.out.println("Mai e emma cu noi? " + puiiMei.contains(childPoc));
      System.out.println(childPoc.hashCode());

      System.out.println("Adolescenta");

      childPoc.setName("Emma");
      System.out.println(childPoc.hashCode());

      System.out.println("Mai e emma cu noi? " + puiiMei.contains(childPoc));
   }
}
@Data
@AllArgsConstructor
class ElementulNostru {
   private String name;
//   private int age;

//   @Override
//   public boolean equals(Object o) {
//      if (this == o) return true;
//      if (o == null || getClass() != o.getClass()) return false;
//      ElementulNostru that = (ElementulNostru) o;
//      return age == that.age && Objects.equals(name, that.name);
//   }
//
//   @Override
//   public int hashCode() {
//      return Objects.hash(name, age);
//   }
}