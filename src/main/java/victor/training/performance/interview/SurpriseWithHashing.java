package victor.training.performance.interview;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode.Exclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;



public class SurpriseWithHashing {



   @Transactional
   public void method() {

      Set<SurpriseWithHashing> set;
      SurpriseWithHashing v = new SurpriseWithHashing();
//      set.add(v);
//      set.add(v);
//      set.add(v);

      // even if you DONT implement hashcode/equals on entities, you can still
      // safely store them in a HashSet (== behavior) WHILE you are in the same @Transaction

//      child1 = repo.findById(childId1);
//      child2 = repo.findById(childId2);
   }
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