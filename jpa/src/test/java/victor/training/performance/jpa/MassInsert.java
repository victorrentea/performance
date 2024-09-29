package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import victor.training.performance.jpa.CaptureSystemOutput.OutputCapture;
import victor.training.performance.jpa.Import.ImportedRecord;
import victor.training.performance.jpa.entity.Country;
import victor.training.performance.jpa.entity.Parent;
import victor.training.performance.jpa.entity.Uber;
import victor.training.performance.jpa.entity.User;
import victor.training.performance.jpa.repo.CountryRepo;
import victor.training.performance.jpa.repo.ParentRepo;
import victor.training.performance.jpa.repo.UberRepo;
import victor.training.performance.jpa.repo.UserRepo;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Slf4j
public class MassInsert {
  @Autowired
  Import massInsert;
  @Autowired
  ParentRepo parentRepo;
  @Autowired
  CountryRepo countryRepo;
  @Autowired
  UserRepo userRepo;
  @Autowired
  UberRepo uberRepo;
  Long userId;

  @BeforeEach
  final void setup() {
    countryRepo.save(new Country(1L, "Romania").setIso2Code("RO"));
    userId = userRepo.save(new User("jdoe")).getId();
  }

  @Test
  @Disabled// TODO enable and fix
  @CaptureSystemOutput
  public void oneUuid(OutputCapture capture) {
    uberRepo.save(new Uber());
    assertThat(capture.toString()).doesNotContainIgnoringCase("SELECT");
  }

  @Test
  public void massInsert() {
    log.info("vvvvvvvvvvv Start mass insert");
    List<ImportedRecord> records = IntStream.range(0, 10)
        .mapToObj(i -> new ImportedRecord("name" + i, "RO", userId))
        .toList();
    massInsert.bulkImport(records);
    log.info("^^^^^^^^^^^ End mass insert");
  }

  @Test
  @Disabled("only a problem in older Hibernate versions")
  public void sequence_1_legacy() {
    parentRepo.save(new Parent());
    parentRepo.save(new Parent());
    parentRepo.save(new Parent());
    parentRepo.save(new Parent());
    parentRepo.save(new Parent());

    // You should only see 1-2 SELECT from the sequence
  }
}
