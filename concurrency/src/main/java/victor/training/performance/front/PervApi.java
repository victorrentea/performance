package victor.training.performance.front;

import victor.training.performance.util.PerformanceUtil;

import java.time.LocalDateTime;
import java.util.List;

public class PervApi {
  public record PervResponse(List<String> data, LocalDateTime nextToken){}

  public static PervResponse fetchRachete(LocalDateTime token, int maxDelta) {
    PerformanceUtil.sleepMillis(100);
    return new PervResponse(List.of("r1","r2"), LocalDateTime.now());
  }
  public static PervResponse fetchToalete(LocalDateTime token, int maxDelta) {
    PerformanceUtil.sleepMillis(5*maxDelta);
    return new PervResponse(List.of("w1","w2"), LocalDateTime.now());
  }
}
