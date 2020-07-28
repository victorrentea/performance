package victor.training.performance.interview;

import lombok.ToString;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PierdemUnElementInMap {
   public static void main(String[] args) {
      Set<Copil> puiiMei = new HashSet<>();

      Copil childOne = new Copil("Emma");
      puiiMei.add(childOne);

      System.out.println(puiiMei.contains(childOne));

      System.out.println(childOne.hashCode());
      // adolescenta
      childOne.setName("Emma-Simona");

      System.out.println(childOne.hashCode());
      System.out.println(puiiMei.contains(childOne));
      puiiMei.forEach(System.out::println);
   }
}

@ToString
class Copil {
   private String name;

   public Copil(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Copil copil = (Copil) o;
      return Objects.equals(name, copil.name);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name);
   }
}
