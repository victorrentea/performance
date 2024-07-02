package victor.training.performance.interview;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Objects;

public class LoosingElementsInASet {
  public static void main(String[] args) {
    HashSet<Child> hashSet = new HashSet<>();

    Child emma = new Child();
    emma.setName("Emma");
    hashSet.add(emma);

    System.out.println(hashSet.contains(emma));
    System.out.println(emma.hashCode());
    emma.setName("Emma-Simona");
    System.out.println(emma.hashCode());

    System.out.println(hashSet.contains(emma));
    hashSet.add(emma);

    System.out.println("Puii meiL: " + hashSet);
  }


//  @Entity // NU PUI @Data pe @Entity ORM
//  @Data //generates hashCode/equals on all fields
  @Getter
  @Setter
  private static class Child {
    // REGULA ORICE CAMP INCLUZI IN HASH/EQUALS el tre sa fie IMUTABIL
    private /*final*/ Long id;
    private /*final*/ String name;


    // evita sa faci hash/eq pe @Entity. Daca totusi insisti, cauta o "cheie naturala". NU include @Id in hash/eq
//  @Override
//  public boolean equals(Object o) {
//    if (this == o) return true;
//    if (o == null || getClass() != o.getClass()) return false;
//    Child child = (Child) o;
//    return Objects.equals(name, child.name);
//  }
//
//  @Override
//  public int hashCode() {
//    return Objects.hashCode(name);
//  }
}
}
