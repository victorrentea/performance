package victor.training.performance.interview;

import lombok.Data;

import java.util.HashSet;

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

  @Data //generates hashCode/equals on all fields
  private static class Child {
    // REGULA ORICE CAMP INCLUZI IN HASH/EQUALS el tre sa fie IMUTABIL
    private /*final*/ Long id;
    private /*final*/ String name;
  }
}
