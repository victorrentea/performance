package victor.training.performance.jfr.events;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;

// TODO check this out : http://hirt.se/blog/?tag=jfr
@Category("Business")
//@Threshold("100 ms")
public class CheckStockJFREvent extends Event {

   @Label("Stock Symbol")
   private String symbol;
   @Description("Return HTTP Status code")
   private int statusCode;
   // only primitives allowed

   public void setSymbol(String symbol) {
      this.symbol = symbol;
   }

   public void setStatusCode(int statusCode) {
      this.statusCode = statusCode;
   }
}
