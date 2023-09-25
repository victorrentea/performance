package victor.training.performance.interview;

import lombok.Data;

import java.util.HashSet;

public class LoosingElementsInASet {
  public static void main(String[] args) {
    HashSet<Child> hashSet = new HashSet<>();

    Child emma = new Child();
    emma.setName("Emma");
    hashSet.add(emma);

    emma.setId(1L); // imagine Hibernate assigns an ID later
    hashSet.add(emma);

    System.out.println("My children: " + hashSet);
  }

  @Data
  private static class Child {
    Long id;
    String name;
  }
}
