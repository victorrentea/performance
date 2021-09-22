package victor.training.performance.interview;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

public class AtentieCuHashCode {
   public static void main(String[] args) {
      Copil child1 = new Copil();

      child1.setName("Emma");

      Set<Copil> copii = new HashSet<>();
      copii.add(child1);

      System.out.println(child1.hashCode());
      System.out.println(copii.contains(child1));

      child1.setName("Emma-Simona");
      System.out.println(child1.hashCode());
      System.out.println(copii.contains(child1)); //daca ==

      Copil childAltul = new Copil();
      childAltul.setName("Emma-Simona");
      copii.add(childAltul);


      System.out.println(copii);

   }
}
// RETAINED size = 101 MB
class Pair { // 12 bytes == cei doi pointeri x4 b  (64 bit) = shallow size
   private String x = "100MB";
   private int[] arr = {/*1MB*/};
}
@Data
class Copil {
   private Long id;

   private String name;
//   @Exclude
//   @Getter @Setter
   private String phone;

   @Override
   public String
   toString() {
      return "Copil{" +
             "name='" + name + '\'' +
             ", phone='" + phone + '\'' +
             '}';
   }
// daca equals ==> hashCode=hashCode; invers NU neaparat
//   public int hashCode() {
//      return 1;
//   }


//   @Override
//   public boolean equals(Object o) {
//      if (this == o) return true;
//      if (o == null || getClass() != o.getClass()) return false;
//      Copil copil = (Copil) o;
//      return Objects.equals(name, copil.name) && Objects.equals(phone, copil.phone);
//   }
//
//   @Override
//   public int hashCode() {
//      return Objects.hash(name) % 100;
//   }
}

// concluzii
// nu scrii de mana hashCode > il generezi sau Lombok
// atentie sa nu se modifice campurile implicate in hashCode dupa ce ai pus elem intr-un HashMap/HashSet
