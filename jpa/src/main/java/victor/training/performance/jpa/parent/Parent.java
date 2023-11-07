package victor.training.performance.jpa.parent;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import victor.training.performance.jpa.uber.Country;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

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
//       fetch = EAGER, // = ask Hibernate to only give you instances of Parent with children pre-loaded
       // by default hibernate fetches children by 1 query/parent
       // defapt EAGER e folosit doar ca sa evite lazy loadingul, dar n-ai scapat de N queryuri.
       // ba mai rau, ori de cate ori primesti isntanta de Parent de la Hibernate, hib a adus si copiii, ca ai ca n-ai nevoie, sunt cu time
       cascade = PERSIST)
//   @BatchSize(size=10) // Hibernate magic that avoids N+1 using ID IN (?,?..,?10)
//   @BatchSize(size=2) // still too many network calls
//   @BatchSize(size=1000) // memory pressure
   private Set<Child> children = new HashSet<>();

   @ManyToOne//(fetch = LAZY)
   private Country country; // surprise !

   protected Parent() { // MUST HAVE no-arg constructor, devi nu pot chema constructorul
   }

   public Parent(String name) { // eu doar pe asta pot sa-l chema
      this.name = Objects.requireNonNull(name);
   }

   public Parent addChild(Child child) {
      children.add(child);
      child.setParent(this);
      return this;
   }
}