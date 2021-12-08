package victor.training.performance.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NamedQueries( {
    @NamedQuery(name = "ce puii meu de nume",
    query = "SELECT p FROM Parent p")
})
public class Parent {
   @Id
   @GeneratedValue
   private Long id;

   private String name;
   private Integer age;

//   @BatchSize(size = 20) // rapid, minim invaziv, dubios?
   @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST/*, fetch = FetchType.EAGER*/)
   // @BatchSize(size=10) // too much magic
   private Set<Child> children = new HashSet<>();

   @ManyToOne//(fetch = FetchType.LAZY)
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