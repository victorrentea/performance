package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.PerformanceUtil;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
public class Leak16_OffHeap {
  public static final int MB100 = 100 * 1024 * 1024;

  @GetMapping("leak16")
  public String offHeap() {
    CompletableFuture.runAsync(() -> {
      log.info("start");
      ByteBuffer byteBuffer = ByteBuffer.allocateDirect(MB100);
      PerformanceUtil.sleepMillis(10000);
      log.info("end");
    });
    return "Heap doesn't seem grow much";
  }
}
