package victor.training.performance.jpa;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.LobCreator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.util.PerformanceUtil;

import javax.persistence.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import static victor.training.performance.util.PerformanceUtil.printUsedHeap;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false) // don't wipe the data
public class LobTest {
   @Autowired
   ProfRepo profRepo;
   @Autowired
   ProfDataRepo profDataRepo;
   @Autowired
   EntityManager entityManager;

   @Test
   void splitEntity() {
      Prof prof = profRepo.save(new Prof().setName("Tavi"));
      profDataRepo.save(new ProfData().setProf(prof).setCvMarkdown("**MARE CHEF**"));

      TestTransaction.end();
      TestTransaction.start();

      // TODO Goal: you should NOT see cvMarkdown SELECTED below
      Prof loadedProf = profRepo.findById(prof.getId()).get();
      System.out.println("Name: " + loadedProf.getName());
      // if you need data:
      // TODO
   }

   @Test
   void streamFromFileInOutDb() throws IOException, SQLException {
      File tempFile = new File("glowroot.jar"); // 10 MB
      System.out.println("File size: " + PerformanceUtil.formatSize(tempFile.length()));

      printUsedHeap("Initial");

      // ============= WRITING ==================
      Session hibernateSession = (Session) entityManager.getDelegate();
      LobCreator lobCreator = Hibernate.getLobCreator(hibernateSession);

      InputStream fileInputStream = new FileInputStream(tempFile);
      Blob blob = lobCreator.createBlob(fileInputStream, tempFile.length());


      Prof prof = new Prof()
          .setName("Tavi")
          .setCvPdf(blob);
      Long profId = profRepo.save(prof).getId();
      printUsedHeap("Before Commit");
      TestTransaction.end();
      fileInputStream.close();

      printUsedHeap("After Commit");

      // ============= READING ==================
      TestTransaction.start();

      Prof profLoaded = profRepo.findById(profId).get();

      log.info("Loaded prof name = " + profLoaded.getName());
      printUsedHeap("After loaded entity");

      // usually profLoaded.getCvPdf().getBinaryStream is copied on disk / http response
      byte[] bytes = IOUtils.toByteArray(profLoaded.getCvPdf().getBinaryStream());
      System.out.println("blob size: " + PerformanceUtil.formatSize(bytes.length));
      printUsedHeap("With byte[] in memory");
   }

}

interface ProfRepo extends JpaRepository<Prof, Long> {

}

interface ProfDataRepo extends JpaRepository<ProfData, Long> {
   ProfData findByProfId(Long profId);
}

@Entity
@Data
class Prof {
   @Id
   @GeneratedValue
   private Long id;
   private String name;

   @Basic(fetch = FetchType.LAZY)
   @Lob // CLOB
   private String cvMarkdown; // BIG and dangerous, TODO move away

   @Lob // BLOB
   private Blob cvPdf;
}

@Entity
@Data
class ProfData {
   @Id
   @GeneratedValue
   private Long id;

   @OneToOne
   // TODO @MapsId // shared PK with Prof https://vladmihalcea.com/the-best-way-to-map-a-onetoone-relationship-with-jpa-and-hibernate/
   private Prof prof;

   @Lob
   private byte[] cvPdf; // BLOB

   @Lob
   private String cvMarkdown; // CLOB
}

//Option 1: Parent <---OneToOne-- Child with CLOB + use ChildRepo to fetch by parent
//Option 2 (magic): Parent ---OneToOne(fetch=LAZY)--> large Child containig CLOB
//Option 3 (more magic, deprecated?): Parent.@Basic(fetch=LAZY) @Lob String data; - requires maven build plugin of
//> https://stackoverflow.com/questions/18423019/how-to-enable-load-time-runtime-weaving-with-hibernate-jpa-and-spring-framewor/18423704


