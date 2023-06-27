package victor.training.performance.jpa;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CumPierziUnElementInHashSet {
  public static void main(String[] args) {
    Set<Copil> puiiMei = new HashSet<>();
    Copil emma = new Copil("Emma");
    puiiMei.add(emma);
    System.out.println(emma.hashCode());
    emma.setName("Emma-Simona");
    System.out.println(emma.hashCode());
    puiiMei.add(emma);
    System.out.println(puiiMei.size());
  }
}

class Copil {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Copil(String name) {
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