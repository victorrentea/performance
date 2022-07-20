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

   @OneToMany(
           /*fetch = FetchType.EAGER,*/ // ineficient dpdv memorie:
           // oricand oriunde in app incarci un Parent, Hibernate aduce si copchii.
           mappedBy = "parent", cascade = CascadeType.PERSIST)
   @BatchSize(size=1000) // too much magic
   private Set<Child> children = new HashSet<>();

   @ManyToOne
   private Country country; // surprise !

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