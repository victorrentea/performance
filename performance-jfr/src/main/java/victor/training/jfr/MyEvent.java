package victor.training.jfr;

import jdk.jfr.Event;

public class MyEvent extends Event {
   private Long orderId;

   public void setOrderId(Long orderId) {
      this.orderId = orderId;
   }
}
