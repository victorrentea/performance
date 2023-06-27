package victor.training.performance.jpa;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@NamedQuery(name= "Parent.cuCopii",
   query = "SELECT p FROM Parent p LEFT JOIN FETCH p.children LEFT JOIN FETCH p.country")
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

   // VIctor niciodata nu implementeaza hash/equals pe entity, si asata nu strica nimic !!
   // cum puii mei ?!
   // daca nu implementez hashCode/equals pe un Object, cand vor fi 2 elem elminate dintrun HashSet<Parent>
   // in acest set nu vor putea exista 2 Parent == unul cu altul

   // cata vreme sunt in aceeasi tranzactie, Hiberante imi va da EXACT ACEEASI REFERINTA la un Parent cu acelasi ID
   // (thanks to 1st level cache)
   // Parent{id:5} == Parent{id:5}


//   @Override
//   public boolean equals(Object o) {
//      if (this == o) return true;
//      if (o == null || getClass() != o.getClass()) return false;
//      Parent parent = (Parent) o;
//      return Objects.equals(id, parent.id) && Objects.equals(name, parent.name) && Objects.equals(age, parent.age) && Objects.equals(children, parent.children) && Objects.equals(country, parent.country);
//   }
//   @Override
//   public int hashCode() {
//      return Objects.hash(id, name, age, children, country); // GRESIT pt ca
//      // usecaseu lui cu bug in pre-prod:
//   }

   // exista
//   @Transactional
//   static {
//      Parent p = new Parent(); // id = null
//      Set<Parent> set = new HashSet<>();
//      set.add(p);
//      repo.save(p); // acum ID-ul e diferit => hashCode da alt raspuns
//      set.add(p); // poti adauga de 2 ori acelasi parinte
//   }
   // Clash of Titans:  Vlad vs Thorben
   // Vlad: fa hashCode pe natural key, daca ai Eg CNP, CUI,
   // Thorben: nu fa hashCode/equals pe @Entity ca n-are sens
}