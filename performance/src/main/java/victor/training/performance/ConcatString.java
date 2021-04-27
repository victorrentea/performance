package victor.training.performance;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcatString {
   private static final Logger log = LoggerFactory.getLogger(ConcatString.class);
   public static void main(String[] args) {

      m(new ConcatString());
   }

   private static void m(ConcatString args) {
//      if (log.isDebugEnabled()) {
         log.debug("BEFORE{}AFTER",args);
//      }
   }
}
