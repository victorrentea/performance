package victor.training.performance.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.PERSIST;

//@SequenceGenerator(name = "parent_seq", sequenceName = "parent_seq", allocationSize = 1) // older versions of Hibernate
@Getter
@Setter
@Entity
public class Parent {
   @Id
   @GeneratedValue// TODO ⚠️older versions might do (strategy = GenerationType.SEQUENCE, generator = "parent_seq")
   private Long id;

   private String name;
   private Integer age;

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