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

   @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST/* ,fetch = FetchType.EAGER*/)
   // never because it will tell Hibe to LOAD the children every single time it gives you a Parent entity
    @BatchSize(size=10) // too much magic
   private Set<Child> children = new HashSet<>();

   @ManyToOne
           (fetch = FetchType.LAZY) // avoid as it involves magic (proxhying the COutnry entity)
   private Country country; // surprise ! links to reference data result in N queries if you bring the parents with a JPQL
//   private Long countryId;

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