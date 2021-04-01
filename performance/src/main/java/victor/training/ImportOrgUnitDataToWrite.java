package victor.training;

public class ImportOrgUnitDataToWrite {
   ImportOrgUnit data;
   private String alteDate;
   private String operatieDeFacut;
   private String errorCode;

   private Exception error;

   public static ImportOrgUnitDataToWrite error(String message) {
      return null;
   }
}

class ProcessOutput<T> {
   T date;
   Exception error;
}