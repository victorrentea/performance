package victor.training.performance.jpa.uber;

import lombok.Value;
import victor.training.performance.jpa.uber.UberEntity.Status;


@Value
public class UberStatusDto {
   Long id;
   Status status;
}
