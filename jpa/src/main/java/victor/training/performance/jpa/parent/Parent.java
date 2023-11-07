package victor.training.performance.jpa.parent;

import lombok.Getter;
import lombok.Setter;
import victor.training.performance.jpa.uber.Country;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Parent {
   @Id
   @GeneratedValue
   private Long id;

   private String name;
   private Integer age;

   @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
   // @BatchSize(size=10) // Hibernate magic that avoids N+1 using ID IN (?,?..)
   private Set<Child> children = new HashSet<>();

   @ManyToOne
   private Country country; // surprise !

   protected Parent() {
   }

   public Parent(String name) {
      this.name = name;
   }

   public Parent addChild(Child child) {
      children.add(child);
      child.setParent(this);
      return this;
   }
}