package victor.training.performance.leak.obj;

import org.apache.commons.lang.RandomStringUtils;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class BigObject1KB implements Serializable {
   public String largeString;
   private static AtomicInteger counter = new AtomicInteger();

   public BigObject1KB() {
      largeString = counter.incrementAndGet() + " - " + RandomStringUtils.random(500, true, false);
   }

   public String getLargeString() {
      return largeString;
   }
}