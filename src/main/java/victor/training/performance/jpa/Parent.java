package victor.training.performance.jpa;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

// NU TREBUIE IMPLEMENTAT HASHCODE EQUALS PE PARINTI.
// dar atunci, cum merge Set<Parent>?
// obiecte care nu implem hash/equals sunt eliminate din Set
// daca sunt == intre ele (aceeasi referinta)
@Getter
@Setter
@Entity
public class Parent {
   @Id
   @GeneratedValue
   private Long id;

   private String name;
   private Integer age;

   //		daca parent are 10000 de copii maxim. nu e o idee grozava sa zici parent.children
   // daca ai multi copii, lasa doar unidirectionala de la COPIL -> PARINTE (many to one)
   @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST/*, fetch = FetchType.EAGER*/)
//    @BatchSize(size=10) // too much magic
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