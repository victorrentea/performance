package victor.training.performance.jpa;

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
import java.util.List;

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

      so.getStudenti().add(victor);
//      victor.getCursuri().add(so); // e gresit ca lipseste. Modelul de Java este inconsistent acum
// sunt inca in tranzactie deschisa cu entitea Victor atasata

      TestTransaction.end(); // Hibernate face auto-flush pe modificarile aparute dupa save();
      // in hib nu mai e nimic
      TestTransaction.start();

      Student student = studentRepo.findById(victor.getId()).get();
      System.out.println(student.getCursuri());

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

   @ManyToMany(mappedBy = "studenti") // unidirectional
   private List<Curs> cursuri = new ArrayList<>();

}

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

   @ManyToMany
   private List<Student> studenti = new ArrayList<>();

}

interface CursRepo extends JpaRepository<Curs, Long> {

}