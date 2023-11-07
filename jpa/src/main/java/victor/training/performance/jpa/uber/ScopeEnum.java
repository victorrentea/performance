package victor.training.performance.jpa.uber;

import java.util.stream.Stream;

public enum ScopeEnum {
   GLOBAL("G"),
   REGION("R"),
   COUNTRY("C"),
   AREA("A"),
   CITY("Y");

   public final String dbCode;
   ScopeEnum(String dbCode) {
      this.dbCode = dbCode;
   }
   public static ScopeEnum fromDbCode(String dbCode) {
      return Stream.of(values()).filter(e -> e.dbCode.equals(dbCode)).findFirst()
          .orElseThrow(() -> new IllegalArgumentException("No Scope for db code: '" + dbCode + "'"));
   }
}
