package victor.training.performance.concurrency;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
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
      // cade la equals
      System.out.println("Mai e emma cu noi? " + puiiMei.contains(new ElementulNostru("Emma-Simona")));
      // cade la hashCode - nici nu ajunge sa se uite in galeata corecta.
      System.out.println("Mai e emma cu noi? " + puiiMei.contains(new ElementulNostru("Emma"))); // numele nou
   }

//   public void bugDupa13AniDeExp() {
//      Parinte parinte = new Parinte();
//      parinte.copchii.add(new Child()); // pus fara ID
//      repo.save(parinte); // copilul capata ID generate de Hibernate > si isi schimba hashcodeul
//      parinte.copchii.add(new Child());
//   }
}

@Entity
class Parinte {
   @Id
   @GeneratedValue
   private Long id;
   @OneToMany(cascade = CascadeType.ALL)
   Set<Child> copchii = new HashSet<>();
}
@Entity
@Data // MARE greseala
class Child {
   @Id
   @GeneratedValue
   private Long id; // atribuit la SAVE de hibernate, magic
   private String name;

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Child child = (Child) o;
      return Objects.equals(id, child.id) && Objects.equals(name, child.name);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, name);
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