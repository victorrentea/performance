package victor.training;


import io.vavr.control.Try;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import javax.transaction.Status;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class DBConstants {
   public static final String JOB_STATUS_DONE = "50";
}

class ImportProcessingControl {
   LocalDateTime importEntry;
   LocalDateTime processBegin;
   ImportStatus status;

   public LocalDateTime getImportEntry() {
      return importEntry;
   }

   public void setImportEntry(LocalDateTime importEntry) {
      this.importEntry = importEntry;
   }

   public LocalDateTime getProcessBegin() {
      return processBegin;
   }

   public void setProcessBegin(LocalDateTime processBegin) {
      this.processBegin = processBegin;
   }

   public ImportStatus getStatus() {
      return status;
   }

   public void setStatus(ImportStatus status) {
      this.status = status;
   }
}
class ImportProcessingJournal {
   Integer importSeq;
   LocalDateTime importDate;

   public Integer getImportSeq() {
      return importSeq;
   }

   public void setImportSeq(Integer importSeq) {
      this.importSeq = importSeq;
   }

   public LocalDateTime getImportDate() {
      return importDate;
   }

   public void setImportDate(LocalDateTime importDate) {
      this.importDate = importDate;
   }
}

enum ImportStatus {
   DUPLIATED("DU"),
   EXPIRED("EX"),
   MISSING("MI");

   private final String code;

   ImportStatus(String code) {

      this.code = code;
   }

   public String getCode() {
      return code;
   }
//   + MAGIE sa ii spui lui MyBatis sa stie sa mapeze VARCHAR la ImportStatus cu un TypeMapper
   // https://mybatis.org/mybatis-3/configuration.html#typeHandlers
   public ImportStatus fromCode(String code) {
      for (ImportStatus value : ImportStatus.values()) {
         if (value.getCode().equals(code)) {
            return value;
         }
      }
      throw new IllegalArgumentException("Not a valid code: "+code);
   }
}

enum ImportLineStatus{
   STARTED,
   WAIT,
   DONE,
   FAILED
}


@Retention(RetentionPolicy.RUNTIME)
@Transactional(rollbackFor = Throwable.class)
@interface PssTransactional {

}

@MapperScan("victor.training")
@SpringBootApplication
public class ProcessOrganisationUnit {
   private static final Logger log = LoggerFactory.getLogger(ProcessOrganisationUnit.class);

   @Autowired
    private MyDBParam dbParam;

   public static final int COMMIT_COUNT = 5000;
   DataSource dataSource;

//   @Transactional(rollbackFor = {SQLException.class, RuntimeException.class})
//   @Transactional(rollbackFor = Throwable.class)
   @PssTransactional
   public void process(String importId) {
      if (importId == null) {
         throw new IllegalArgumentException();
      }



      ImportProcessingControl control = null;
      ImportProcessingJournal journal = null;

      LocalDateTime importDate;
      if (journal != null && journal.getImportDate() != null) {
         importDate = journal.getImportDate();
      } else {
         importDate = control.getImportEntry();
      }
      int maxRow = -1;

      try {

      } catch (Exception e) {
//         if (maxRow == 1 ) nu atinge coloana
      }


//      dataSource.getConnection()

      JdbcTemplate jdbc = new JdbcTemplate(dataSource);


      List<ImportOrgUnit> list = jdbc.query("SELECT ROWID," +
                                            "   entry_id," +
                                            "   import_correlation_id," +
                                            "   correlation_entry_no," +
                                            "   operation," +
                                            "   row_status," +
                                            "   org_unit_id," +
                                            "   TRIM (ext_org_unit_code)," +
                                            "   parent_org_unit_id," +
                                            "   ext_parent_org_unit_code," +
                                            "   TRIM (org_unit_name) " +
                                            "  FROM bo_import_org_unit" +
                                            " WHERE import_jrn_id  = ? " +
                                            "   AND COALESCE (row_status, ' ') < ?" +
                                            " ORDER BY correlation_entry_no",
          (rs, rowNum) -> extractFromResult(rs),
          importId, DBConstants.JOB_STATUS_DONE);

      List<ImportOrgUnit> importOrgUnits = mapper.selectData(importId);

      for (ImportOrgUnit unit : importOrgUnits) {
          // inacceptabil pt volume mari
      }

//      jdbc.batch


//      prepareImport(); // arbitrary batch-specific logic
      int chunkSize = 500;
      int lastPageStart = 0;
      while (true) {

         try {
            List<ImportOrgUnit> page = readPage(lastPageStart, chunkSize);
            if (page.size() == 0) {
               return;
            }

            List<ImportOrgUnitDataToWrite> pageToInsert = processor(page);
            // mybatis batch INSERT https://stackoverflow.com/questions/23486547/mybatis-batch-insert-update-for-oracle

            TransactionTemplate transactionTemplate = new TransactionTemplate();
            transactionTemplate.setPropagationBehaviorName("REQUI");
                transactionTemplate.execute(status -> {

                   writePage(pageToInsert); // in propria tranzactie
//                   return Status.STATUS_MARKED_ROLLBACK ~~~ gen
                   return null;
                }
            );
            lastPageStart += page.size();

            // increment counters + statistics
         } catch (Exception e) {
            // chunk failed
            // TODO retry tot fluxul din catch dar cu chunk size = 1
            // poti face mai destept: divide & impera

            // cand gasesti randul cu buba, il marchezi cu eroare si treci peste.
         }
      }



//      Connection connection;

//      PreparedStatement ps = connection.prepareStatement("INSERT");
//      ps.setString("a",1);
//      ps.addBatch();
//      ps.setString("a",2);
//      ps.addBatch();
//      ps.executeBatch();


   }

   private void writePage(List<ImportOrgUnitDataToWrite> page) {
      List<Map<String, Object>> insertParams = new ArrayList<>();
      List<Map<String, Object>> deleteAttrs = new ArrayList<>();
      List<Map<String, Object>> insertAttrs = new ArrayList<>();
      List<Map<String, Object>> insertAltceva = new ArrayList<>();

//       ORDINEA CONTEAZA

      for (ImportOrgUnit importOrgUnit : page) {

//         if (effOperation == INSERT) {
            // AICI sa faci un INSERT
//         } else if (effOperation == INSERT) {
            insertParams.add(Map.of("id", 1,
                "name", "aaaa"));
//         }
            // AICI UPDATE
      }
      NamedParameterJdbcTemplate inserts = new NamedParameterJdbcTemplate(dataSource);
      inserts.batchUpdate("INSERT INTO X() VALUES (:id, :name)", insertParams.toArray(new Map<>()[0]));
   }

   private Try<ImportOrgUnitDataToWrite> processor(List<ImportOrgUnit> page) {

      // fetch de date pentru validari si calcule. Toate cu "IN" operator.
//      Map<PK, date> map1, map2;
      for (ImportOrgUnit importOrgUnit : page) {

         try {
            results.add(new ImportOrgUnitDataToWrite())
         } catch (Exception e) {
            results.add(ImportOrgUnitDataToWrite.error(e.getMessage()));
         }
      }

      return null;
   }

   private List<ImportOrgUnit> readPage(int lastPageStart, int chunkSize) {
//      return mapper.selectData(id, offset, chunkSize);
      return null;
   }

   @Autowired
   private MyBatisMapper mapper;

   private ImportOrgUnit extractFromResult(ResultSet rs) throws SQLException {
      ImportOrgUnit result = new ImportOrgUnit();
      result.setEntryId(rs.getInt("entry_id"));
      return result;
   }
}


class ImportOrgUnit {
   private Integer importJrnId;
   private Integer entryId;
   private Integer importRowId;
   private Integer importCorrelationId;
   private Integer correlationEntryNo;
   private Integer operation;
   private Integer rowStatus;
   private Integer orgUnitId;
   private String extOrgUnitCode;
   private Integer parentOrgUnitId;
   private String extParentOrgUnitCode;
   private String orgUnitName;

   public Integer getImportJrnId() {
      return importJrnId;
   }

   public void setImportJrnId(Integer importJrnId) {
      this.importJrnId = importJrnId;
   }

   public Integer getEntryId() {
      return entryId;
   }

   public void setEntryId(Integer entryId) {
      this.entryId = entryId;
   }

   public Integer getImportRowId() {
      return importRowId;
   }

   public void setImportRowId(Integer importRowId) {
      this.importRowId = importRowId;
   }

   public Integer getImportCorrelationId() {
      return importCorrelationId;
   }

   public void setImportCorrelationId(Integer importCorrelationId) {
      this.importCorrelationId = importCorrelationId;
   }

   public Integer getCorrelationEntryNo() {
      return correlationEntryNo;
   }

   public void setCorrelationEntryNo(Integer correlationEntryNo) {
      this.correlationEntryNo = correlationEntryNo;
   }

   public Integer getOperation() {
      return operation;
   }

   public void setOperation(Integer operation) {
      this.operation = operation;
   }

   public Integer getRowStatus() {
      return rowStatus;
   }

   public void setRowStatus(Integer rowStatus) {
      this.rowStatus = rowStatus;
   }

   public Integer getOrgUnitId() {
      return orgUnitId;
   }

   public void setOrgUnitId(Integer orgUnitId) {
      this.orgUnitId = orgUnitId;
   }

   public String getExtOrgUnitCode() {
      return extOrgUnitCode;
   }

   public void setExtOrgUnitCode(String extOrgUnitCode) {
      this.extOrgUnitCode = extOrgUnitCode;
   }

   public Integer getParentOrgUnitId() {
      return parentOrgUnitId;
   }

   public void setParentOrgUnitId(Integer parentOrgUnitId) {
      this.parentOrgUnitId = parentOrgUnitId;
   }

   public String getExtParentOrgUnitCode() {
      return extParentOrgUnitCode;
   }

   public void setExtParentOrgUnitCode(String extParentOrgUnitCode) {
      this.extParentOrgUnitCode = extParentOrgUnitCode;
   }

   public String getOrgUnitName() {
      return orgUnitName;
   }

   public void setOrgUnitName(String orgUnitName) {
      this.orgUnitName = orgUnitName;
   }
}