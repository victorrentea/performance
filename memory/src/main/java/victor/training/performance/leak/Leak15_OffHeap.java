package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import static victor.training.performance.util.PerformanceUtil.sleepSeconds;

@Slf4j
@RestController
public class Leak15_OffHeap {
  public static final int MB100 = 100 * 1024 * 1024;

  @GetMapping("leak15")
  public String offHeap() {
    CompletableFuture.runAsync(() -> {
      log.info("start");
      ByteBuffer byteBuffer = ByteBuffer.allocateDirect(MB100);
      sleepSeconds(10);
      log.info("end");
    });
    return "Heap doesn't grow much";
  }
}
