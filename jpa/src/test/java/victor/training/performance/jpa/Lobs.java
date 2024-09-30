package victor.training.performance.jpa;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.entity.Uber;
import victor.training.performance.jpa.repo.UberRepo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false) // don't wipe the data
public class Lobs {
  @Autowired
  UberRepo uberRepo;
  @Autowired
  EntityManager entityManager;

  String profId;

  @Test
  void streamDataInOutDb() throws IOException, SQLException {
    File tempFile = new File("pom.xml"); // imagine large
    System.out.println("File size bytes: " + tempFile.length());

    // ============= WRITING TO DB ==================
    Session hibernateSession = (Session) entityManager.getDelegate();
    try (Reader reader = new FileReader(tempFile)) {
      Clob clob = hibernateSession.getLobHelper().createClob(reader, tempFile.length());
      Uber uberSaved = new Uber().setContent(clob);
      profId = uberRepo.save(uberSaved).getId();

      TestTransaction.end(); // file is streamed from file in the DB
    }

    // ============= READING FROM DB ==================
    TestTransaction.start();

    Uber uberLoaded = uberRepo.findById(profId).get();
    log.info("Loaded prof name = " + uberLoaded.getName());

    // usually ofloaded to a temp file on disk
    char[] chars = IOUtils.toCharArray(uberLoaded.getContent().getCharacterStream());
    System.out.println("Downloaded clob size bytes: " + chars.length);
  }

}
