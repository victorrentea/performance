package victor.training.performance.jpa.parent;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import victor.training.performance.jpa.uber.Country;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

@Getter
@Setter
@Entity
public class Parent {
   @Id
   @GeneratedValue
   private Long id;

   private String name;
   private Integer age;


   // Zei ai Hibernate-ului: Vlad Mihalcea, Thorben Janssen
   @OneToMany(mappedBy = "parent", cascade = PERSIST,
       fetch = EAGER)// ii spune lui JPA sa incarce copiii din prima
   // ori de cate ori vei primit o instanta de Parent de la JPA, ea va avea lista de copii incarcata
    @BatchSize(size=10) // Hibernate magic that avoids N+1 using ID IN (?,?..,?10)
   private Set<Child> children = new HashSet<>();

   @ManyToOne
   private Country country; // surprise !

   public Parent() {}
   public Parent(String name) {
      this.name = name;
   }

   public Parent addChild(Child child) {
      children.add(child);
      child.setParent(this);
      return this;
   }

   public String toString() {
      return "Parent{id=" + id + ", name='" + name + "'}";
   }
}