package victor.training.performance.jpa;

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

// if the spec tells you that a field of an entity can have only one of 5 values,
// would you
// a) create a table to keep those 5 values ? ID | LABEL -> NO 1: perf 2: QoL of Dev
         // you will learn by heart/notebool COUNTRY has id 6
   // PRO: you can add a new entry w/o a code change
   // fine if in the code you never relate to any of that particular values
      // NOT like this: if(uber.getScope().getId()==SCOPE_COUNTRY) << LOGIC ON IT
// b) enum
   // PRO: dev QoL ==