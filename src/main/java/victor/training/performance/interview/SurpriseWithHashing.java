package victor.training.performance.interview;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode.Exclude;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

public class SurpriseWithHashing {
   public static void main(String[] args) {
      Set<Child> children = new HashSet<>();

      Child childOne = new Child("123123", "Emma");

      children.add(childOne);

      System.out.println(children.contains(childOne));

      System.out.println(childOne.hashCode());
      childOne.setName("Emma-Simona"); // adolescence
      System.out.println(childOne.hashCode());

      System.out.println(children.contains(childOne));
      children.add(childOne);

      System.out.println(children);
   }
}


//@Data // <<< NEVER USE on @Entity
@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
class Child {
//   @Id
//   private Long id;
   private final String cnp;
   @Exclude
   private  String name;


}