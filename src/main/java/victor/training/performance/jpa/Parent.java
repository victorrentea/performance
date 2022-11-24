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
//           , fetch = FetchType.EAGER // asta o pui doar din groaza de LazyInitializationException,
           // nu ca-ti pasa de performanta. ca tot face Hiber N queryuri\
           // de ce zboara Vlad la tine sa-ti spuna ca nu: pt ca tocmai ai incarcat toti copii PESTE TOT unde vreodata scoti parinit din DBF.
   )
    @BatchSize(size=20) // too much magic
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