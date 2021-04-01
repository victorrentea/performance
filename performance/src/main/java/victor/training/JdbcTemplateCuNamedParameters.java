package victor.training;

import io.vavr.collection.Map;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

public class JdbcTemplateCuNamedParameters {
   private static DataSource dataSource;

   public static void main(String[] args, Integer importId) {
      NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(dataSource);



      jdbc.query("SELECT ROWID," +
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
                 " WHERE import_jrn_id  = :importId " +
                 "   AND COALESCE (row_status, ' ') < :rowStatus" +
                 " ORDER BY correlation_entry_no",
//          Map.of(
//              "rowStatus", DBConstants.JOB_STATUS_DONE,
//              "importId", importId),
          (java.util.Map<String, ?>) null,
          (rs, rowNum) -> null
          );
   }
}
