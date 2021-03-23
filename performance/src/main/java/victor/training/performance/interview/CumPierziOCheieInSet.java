package victor.training.performance.interview;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CumPierziOCheieInSet {
   public static void main(String[] args) {

      Set<Copil> puiiMei = new HashSet<>();

      Copil childPoc = new Copil("Emma");
      puiiMei.add(childPoc);
      System.out.println(puiiMei.contains(childPoc));

      System.out.println(childPoc.hashCode());

//      childPoc.setName("Emma-Simona");

      System.out.println(childPoc.hashCode());
      System.out.println(puiiMei.contains(childPoc));
   }
}

class Copil {
   final String name;

   public Copil(String name) {
      this.name = name;
   }

//   public Copil setName(String name) {
//      this.name = name;
//      return this;
//   }

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