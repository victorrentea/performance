package victor.training.performance.jpa.parent;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import victor.training.performance.jpa.uber.Country;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.PERSIST;

@Getter
@Setter
@Entity
public class Parent {
   @Id
   @GeneratedValue
   private Long id;

   private String name;
   private Integer age;

   @BatchSize(size = 10) // Hibernate magic that avoids N+1 using ID IN (?,?..,?10)
   @OneToMany(mappedBy = "parent", cascade = PERSIST)
   // @BatchSize(size=10) // Hibernate magic that avoids N+1 using ID IN (?,?..,?10)
   private Set<Child> children = new HashSet<>();

   @ManyToOne
   private Country country; // surprise !

   public Parent() {}
   public Parent(String name) {
      this.name = name;
   }

   public Parent addChild(Child child) {
      children.add(child);
      child.setParent(this);
      return this;
   }

   public String toString() {
      return "Parent{id=" + id + ", name='" + name + "'}";
   }
}