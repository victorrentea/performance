package victor.training.performance;

import lombok.Data;

import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CumPotiSaPierziElementeInSet {

  public static void main(String[] args) {
    Set<Copil> set = new HashSet<>();

    Copil childPoc = new Copil();
    childPoc.setNume("Emma");
    set.add(childPoc);
    System.out.println("Imi mai recunosc copilul:" + set.contains(childPoc));
    System.out.println("hashu= " + childPoc);
    System.out.println("inta la adolescenta");

//    childPoc.setNume("Emma-Simona"); // n-are voie

    System.out.println("hashu= " + childPoc);
    System.out.println("Imi mai recunosc copilul:"+ set.contains(childPoc));

    set.add(childPoc);
    System.out.println("Cati copii am: " + set);

    // concluzie: elementele puse in hashSet sau in hashMap ca key nu trebuie
    // sa poata sa-si schimbe hashCode
  }
}
@Data // hashcode pe campuri mutabile = PERICOL
class Copil {
  private final String nume; //  #2 mereu final; daca e folosit in hashCode!!!

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Copil copil = (Copil) o;
    return Objects.equals(nume, copil.nume);
  }

  @Override
  public int hashCode() {
//    return nume.length(); // #1 gresit, se acumuleaza toate elementele in acelasi bucket => performanta praf O(N)
    return Objects.hash(nume); // ok
  }
}
