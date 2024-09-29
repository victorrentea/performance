package victor.training.performance.jpa;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

@SpringBootTest
@Transactional
@Rollback(false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MassInsert {
  private static final Logger log = LoggerFactory.getLogger(MassInsert.class);

  @Autowired
  private DocumentRepo documentRepo;
  @Autowired
  private DocumentTypeRepo documentTypeRepo;
  private List<Long> docTypeIds;

  @BeforeEach
  final void before() {
    List<DocumentType> docTypes = IntStream.range(1, 20).mapToObj(i -> "DocType" + i).map(DocumentType::new).collect(toList());
    docTypeIds = documentTypeRepo.saveAll(docTypes).stream().map(DocumentType::getId).collect(toList());
    TestTransaction.end(); // flush and close the Persistence Context
  }

  @Test
  public void importData() {
    long t0 = currentTimeMillis();
    for (int page = 0; page < 20; page++) {
      TestTransaction.start();
      log.debug("--- PAGE " + page);
      for (int i = 0; i < 100; i++) {
        Document document = new Document();
        Long docTypeId = docTypeIds.get(i % docTypeIds.size());
        document.setType(documentTypeRepo.findById(docTypeId).orElseThrow());
        documentRepo.save(document);
      }
      TestTransaction.end(); // flush and close the Persistence Context
    }
    long t1 = currentTimeMillis();
    log.debug("Took {} ms (naive)", t1 - t0);

    // TODO FK to doctype
    // TODO docTypeId = docTypeRepo.findByName(""): preload a Map<String, Long> docTypeNameToId
    // TODO batching inserts
    // TODO identifiers: Sequence size (@see gaps!), IDENTITY, UUID
  }
}

// When using a UUID as PK:
// @GenericGenerator(name = "uuid", strategy = "victor.training.jpa.perf.UUIDGenerator") +  @GeneratedValue(generator = "uuid") private String id;
@Entity
@Getter
@Setter
 class Document {
  @Id
  @GeneratedValue
  private Long id;
  @ManyToOne
  private DocumentType type;
}
interface DocumentRepo extends JpaRepository<Document, Long> {
}
@Entity
@Getter
@Setter
 class DocumentType {
  @Id
  @GeneratedValue
  private Long id;
  private String label;
  public DocumentType() {}

  public DocumentType(String label) {
    this.label = label;
  }
}
interface DocumentTypeRepo extends JpaRepository<DocumentType, Long> {
}
