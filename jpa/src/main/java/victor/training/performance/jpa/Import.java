package victor.training.performance.jpa;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import victor.training.performance.jpa.entity.Country;
import victor.training.performance.jpa.entity.Uber;
import victor.training.performance.jpa.entity.User;
import victor.training.performance.jpa.repo.CountryRepo;
import victor.training.performance.jpa.repo.UberRepo;
import victor.training.performance.jpa.repo.UserRepo;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class Import {
  public static final int ITEMS_PER_PAGE = 5;
  private final UberRepo uberRepo;
  private final UserRepo userRepo;
  private final CountryRepo countryRepo;
  private final PlatformTransactionManager transactionManager;

  record ImportedRecord(String name, String countryIso2Code, Long userId) {
  }

  public void bulkImport(List<ImportedRecord> allRecords) {
    TransactionTemplate tx = new TransactionTemplate(transactionManager);
    List<List<ImportedRecord>> pages = Lists.partition(allRecords, ITEMS_PER_PAGE);
    for (List<ImportedRecord> page : pages) {
      tx.executeWithoutResult(status -> savePageInTx(page));
    }
  }

  private void savePageInTx(List<ImportedRecord> page) {
    log.info("▶️▶️▶️▶️▶️▶️ Start page");
    for (ImportedRecord record : page) {
      Country country = countryRepo.findByIso2Code("RO").orElseThrow();
      User user = userRepo.findById(record.userId()).orElseThrow();
      Uber entity = new Uber()
          .setFiscalCountry(country)
          .setCreatedBy(user);
      uberRepo.save(entity);
    }
  }
}
