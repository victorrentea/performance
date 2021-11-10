package victor.training.performance.jpa;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Parent {
   @Id
   @GeneratedValue
   private Long id;

   private String name;
   private int age;

   @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
   // @BatchSize(size=10) // too much magic
   private Set<Child> children = new HashSet<>();

   private Parent() {
   }

   public Parent(String name) {
      this.name = name;
   }

   public Long getId() {
      return id;
   }

   public Parent addChild(Child child) {
      children.add(child);
      child.setParent(this);
      return this;
   }

   public Set<Child> getChildren() {
      return children;
   }

   public int getAge() {
      return age;
   }

   public Parent setAge(int age) {
      this.age = age;
      return this;
   }

   public String getName() {
      return name;
   }
}