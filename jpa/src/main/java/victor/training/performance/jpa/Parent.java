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

   @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST/*, fetch = FetchType.EAGER*/)
//   @BatchSize(size=10) // Hibernate magic care-i spune sa incarce copiii a max 10 parinti odata
   // pt caz de optimizat app legacy cu minim risc, atunci e bun asta
   // rau: nisa = nu e JPA, magie grea (cum merge? ce parinti incarca in avans), inca exista LAZY LOADING
   private Set<Child> children = new HashSet<>(); // hibernate pune PersistentSet/Bag care lazy-loadeaza colectia

   @ManyToOne
   private Country country; // surprise ! Hibernate face +1 query sa aduca tara

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