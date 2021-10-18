package victor.training.performance.interview;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ImmutableEntity {
   @Id
   @GeneratedValue
   private Long id;

   private String name;
   private int age;

   private ImmutableEntity() {} // just for hibernate

   public ImmutableEntity(String name, int age) {
      this.name = name;
      this.age = age;
   }

   public String getName() {
      return name;
   }

   public int getAge() {
      return age;
   }

   public ImmutableEntity withAge(int newAge) {
      ImmutableEntity copy = new ImmutableEntity(name, newAge);
      copy.id = id;
      return copy;
   }
}


class Ugly {
//   @Transactional
   public static void main(String[] args) {
//      ImmutableEntity e;//repo.find
//      // only OK if e is not 'attached' to hibernate. ie. no surrounding @Transaction.
//      ImmutableEntity newE = e.withAge(18);
//
//      repo.save(newE);
   }

}