package victor.training.performance.jpa;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.annotation.Rollback;
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
   ProfDataRepo dataRepo;
   @Autowired
   EntityManager em;

   @Test
   void test() {
      Prof prof = new Prof().setName("Tavi");
      repo.save(prof);
      dataRepo.save(new ProfData().setProf(prof).setCvMarkdown("**MARE CHEF**"));

      em.flush();
      em.clear();

      List<Prof> profList = repo.findAll();

      for (Prof p : profList) {
         System.out.println(p.getName());
      }
      System.out.println("Pana aici nici un query dupa markdown");
      ProfData data = dataRepo.findByProfId(prof.getId());
      System.out.println("Data " +data) ;

   }
}

interface ProfRepo extends JpaRepository<Prof, Long> {}
interface ProfDataRepo extends JpaRepository<ProfData, Long> {
   ProfData findByProfId(Long profId);
}

//CLOB sau BLOB

@Entity
@Data
class Prof {
   @Id
   @GeneratedValue
   private Long id;
   private String name;

}

@Entity
@Data
class ProfData {
   @Id
   @GeneratedValue
   private Long id;

   @OneToOne
   private Prof prof;

   @Lob
   private byte[] cvPdf; // BLOB

   @Lob
   private String cvMarkdown; // CLOB
}

//strategia 1: Parinte ---OneToOne(fetch=LAZY)--> Copil cu CLOB
//strategia 2: Parinte <---OneToOne-- Copil cu CLOB