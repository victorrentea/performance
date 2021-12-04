package victor.training.performance.primitives.probes;

import lombok.Value;

import java.time.LocalTime;

@Value
public class Sample {
   LocalTime timestamp;
   String probe;
   int value;
}