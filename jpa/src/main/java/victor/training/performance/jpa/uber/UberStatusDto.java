package victor.training.performance.jpa.uber;

import lombok.Value;
import victor.training.performance.jpa.uber.UberEntity.Status;


public record UberStatusDto(Long id, Status status) {
}
