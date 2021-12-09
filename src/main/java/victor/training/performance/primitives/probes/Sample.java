package victor.training.performance.primitives.probes;

import java.time.LocalTime;

public final class Sample {
   private final LocalTime timestamp;
   private final String probe;
   private final int value;

   public Sample(LocalTime timestamp, String probe, int value) {
      this.timestamp = timestamp;
      this.probe = probe;
      this.value = value;
   }

   public LocalTime getTimestamp() {
      return this.timestamp;
   }

   public String getProbe() {
      return this.probe;
   }

   public int getValue() {
      return this.value;
   }

   public String toString() {
      return "Sample(" + this.getProbe() + ", value=" + this.getValue() + ")";
   }
}