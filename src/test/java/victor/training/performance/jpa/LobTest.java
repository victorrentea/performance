package victor.training.performance.jpa;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
      Prof prof = new Prof().setName("Tavi").setCvMarkdown("**MARE CHEF**");
      repo.save(prof);
//      dataRepo.save(new ProfData().setProf(prof).setCvMarkdown("**MARE CHEF**"));

      em.flush();
      em.clear();

      List<Prof> profList = repo.findAll();

      for (Prof p : profList) {
         System.out.println(p.getName());
      }
   }
}

interface ProfRepo extends JpaRepository<Prof, Long> {

}
interface TagRepo extends JpaRepository<Tag, Long> {
   Page<Tag> findTagsByProfId(Long profId, Pageable pageRequest);
}
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

   @Basic(fetch = FetchType.LAZY)
   @Lob
   private String cvMarkdown; // BLOB  // requires load time weaving(load time)  or bytecode enhancement (compile)


//   @OneToMany // asta doar pentru volume de 10-1000 max de elem
//   List<Tag> tags; // ce te faci daca in lista asta sunt 1M de elemente
}

@Entity
@Data
class Tag {
   @Id
   @GeneratedValue
   private Long id;
   @ManyToOne
   Prof prof;
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
//strategia 3 (lenesa): Parinte are @Basic(fetch=LAZY) @Lob String data;


