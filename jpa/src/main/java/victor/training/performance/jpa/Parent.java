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

   @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST
//       fetch = FetchType.EAGER // TOT N+1 face by default
   )
   @BatchSize(size=10) // too much magic
   private Set<Child> children = new HashSet<>();

   @ManyToOne(fetch = FetchType.LAZY) // EAGER by default = JPA nu iti va da niciodata un Parent fara country
   private Country country;
//   = new Country() { // permite asta
//      @Override
//      public String getIso2Code() {
//         return super.getIso2Code();
//      }
//      orice getter chemi trigereaza query
//   }; // surprise !

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
   //hashCode equals default din Object
   // se bazeaza pe identificatorul obiectului clasaMea@1285798

   // in aceeasi tranzactie/query 2 Parent cu acelasi ID vor fi == intre ei
   // = 1st Level Cache
}