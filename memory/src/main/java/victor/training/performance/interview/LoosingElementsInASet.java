package victor.training.performance.interview;

import java.util.HashSet;
import java.util.Objects;

public class LoosingElementsInASet {
  public static void main(String[] args) {
    HashSet<Child> puiiMei = new HashSet<>();

    Child emma = new Child();
    emma.setName("Emma");
    puiiMei.add(emma);

    emma.setName("Emma-Simona");
    puiiMei.remove(emma);

    System.out.println("My children: " + puiiMei);
    System.out.println("Am copchii: " + puiiMei.size());
  }

  //generates hashCode/equals on all fields
  private static class Child {
    private Long id;
    private String name;

    public Child() {
    }

    public Long getId() {
      return this.id;
    }

    public String getName() {
      return this.name;
    }

    public Child setId(Long id) {
      this.id = id;
      return this;
    }

    public Child setName(String name) {
      this.name = name;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      Child child = (Child) o;
      return Objects.equals(id, child.id) && Objects.equals(name, child.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, name);
    }

    public String toString() {
      return "LoosingElementsInASet.Child(id=" + this.getId() + ", name=" + this.getName() + ")";
    }
  }
}
