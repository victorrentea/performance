package victor.training.performance.jpa;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

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

   @OneToMany(mappedBy = "parent",
           //fetch = FetchType.EAGER, // tells hibernate
           // to only ever give the app instances of Parent @Entity
           // with filled children.
           // 1) But those are still (surprise!)
           // brought one after the other (N+1) is still there
           // 2) hurting performance everywhere: every time hibernate gives you an entity of Parent,
           // it will always fetch the children
           // [best practice]: only use if PARENT DOES NOT HAVE ANY
           // MEANING WITHOUT ITS CHILDREN
           cascade = CascadeType.PERSIST)
//   @BatchSize(size=100) // too much magic
   // tells hibernate to load the children of parents in pages of 10
   private Set<Child> children = new HashSet<>();

   @ManyToOne(fetch = FetchType.LAZY) //
   private Country country; // surprise : it's a proxy created around the ID by Hibernate.
   // the first time you acces any getter (except getId()) a SELECT is executed

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
}