package victor.training.performance.jpa;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false)
public class LobTest {
   @Autowired
   ProfRepo repo;
   @Autowired
   EntityManager em;

   @Test
   void test() {
      Prof prof = new Prof()
          .setName("Tavi")
          .setData(new ProfData()
              .setCvMarkdown("**Mare Sef**")
          );
      repo.save(prof);

      em.flush();
      em.clear();

      List<Prof> profList = repo.findAll();
      // aici in lista de profi stau instante de Prof.class care au id,name,si in "data" field o instanta "fictiva" de ProfData
      // acea instanta fictiva
      // class victor.training.performance.jpa.ProfData$HibernateProxy$FvglDxDt
      // NU contine datele luate din DB. pt ca i-am spus @OneToOne(LAZY)
      // Hibernate de fapt a pus in

//      prof.setData(new ProfData(){ // subclasa anonima ~~~ proxy
//         @Override
//         public String getCvMarkdown() {
////            if (!amIncarcatMarkdown) {
////               setCvMarkdown(iau din DB);  !!!!!!! Trebuie inca Tranzactia curenta sa fie deschisa.
////               amIncarcatMarkdown = true;
////            }
//            return super.getCvMarkdown();
//         }
//      });

//      TestTransaction.end(); // asta nu permite proxy-ului sa mai incarce date caci inchide TX: elibereaza (pierde) conexiunea cu DB.

      for (Prof p : profList) {
         System.out.println(p.getName());
         System.out.println(p.getData().getClass());
         System.out.println(p.getData().getCvMarkdown()); // linia asta triggereaza proxy-ul sa faca LAZY load pe sub mana din DB
      }

      // CLOB folosesti chiar si pentru un string cand e mai mare de 2000 caractere.
      // CLOB nu sunt indexabile.    SELECT from PROF where cv like '%UPB%'

   }
}

interface ProfRepo extends JpaRepository<Prof, Long> {}

//CLOB sau BLOB

@Entity
@Data
class Prof {
   @Id
   @GeneratedValue
   private Long id;
   private String name;

   @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private ProfData data;
}

@Entity
@Data
class ProfData {
   @Id
   @GeneratedValue
   private Long id;
//
//   static class HibernateProxy {
//      static class FvglDxDt extends ProfData{
//
//      }
//   }
   @Lob
   private byte[] cvPdf; // BLOB

   @Lob
   private String cvMarkdown; // CLOB
}

//strategia 1: Parinte ---OneToOne(fetch=LAZY)--> Copil cu CLOB
//strategia 1: Parinte <---OneToOne(fetch=LAZY)-- Copil cu CLOB