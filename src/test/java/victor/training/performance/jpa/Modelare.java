package victor.training.performance.jpa;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static lombok.AccessLevel.NONE;

@SpringBootTest
@Transactional
@Rollback(false)
public class Modelare {
   @Autowired
   StudentRepo studentRepo;

   @Autowired
   CursRepo cursRepo;

   @Test
   void test() {
      Curs so = cursRepo.save(new Curs().setName("SO"));
      Student victor = studentRepo.save(new Student().setName("Victor"));

      so.addStudent(victor);
//      victor.getCursuri().add(so); // e gresit ca lipseste. Modelul de Java este inconsistent acum
// sunt inca in tranzactie deschisa cu entitea Victor atasata

      TestTransaction.end(); // Hibernate face auto-flush pe modificarile aparute dupa save();
      // in hib nu mai e nimic
      TestTransaction.start();

      Student victor2 = studentRepo.findById(victor.getId()).get();
//      System.out.println(student.getCursuri());

      Curs so2 = cursRepo.findById(so.getId()).get();
      so2.removeStudent(victor2);

      TestTransaction.end();
      TestTransaction.start();


   }
}

@Getter
@Setter
@NoArgsConstructor
@Entity
class Student {
   @Id
   @GeneratedValue
   private Long id;
   private String name;

   @Setter(AccessLevel.NONE)
   @ManyToMany(mappedBy = "studenti") // unidirectional
   List<Curs> cursuri = new ArrayList<>();


   public List<Curs> getCursuri() {
      return Collections.unmodifiableList(cursuri);
   }
}
// STUDENTUL NU ARE VOIE SA ISI MODIFIC ECURSURILE. SCHIMBARILE SE INTAMPLA DOAR PRIN CURS (add/remove student)

interface StudentRepo extends JpaRepository<Student, Long> {

}

@Getter
@Setter
@NoArgsConstructor
@Entity
class Curs {
   @Id
   @GeneratedValue
   private Long id;
   private String name;

   @Setter(NONE)
   @ManyToMany
   private List<Student> studenti = new ArrayList<>();

   public List<Student> getStudenti() {
      return Collections.unmodifiableList(studenti);
   }

   public void addStudent(Student student) {
      studenti.add(student);
      student.cursuri.add(this);
   }

   public void removeStudent(Student student) {
      studenti.remove(student);
      student.cursuri.remove(this);
   }
}

interface CursRepo extends JpaRepository<Curs, Long> {

}