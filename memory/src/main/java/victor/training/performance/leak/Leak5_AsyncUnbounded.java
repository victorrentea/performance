package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.h2.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak5")
public class Leak5_AsyncUnbounded {
  @Autowired
  private Worker worker;
  private static final AtomicInteger index = new AtomicInteger(0);
// TODO fix index.html

//  private static final ExecutorService myOwnExecutor = newFixedThreadPool(5);
  private static final ExecutorService myOwnExecutor =new ThreadPoolExecutor(
    5,
    5,
    1, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(1000));
  @PostMapping
  public String endpoint(
      @RequestParam(value = "file", required = false)
      MultipartFile multipartFile) throws IOException {
    System.out.println("In");

//    byte[] contents100MB = multipartFile.getBytes(); // OOME risk: concurrent large uploads
    Path tempPath = Files.createTempFile("tmp", "data-" + System.currentTimeMillis() + "-" + UUID.randomUUID());
    IOUtils.copy(multipartFile.getInputStream(), Files.newOutputStream(tempPath)); // can copy 1GB with a few kb of buffer in RAM
    // next level : Java NIO

    myOwnExecutor.submit(() -> worker.processFile(index.incrementAndGet(), tempPath));
    // asign the work to a thread pool of 5 threads max => max 500 MB in mem

    return "Keep calling this 20 times within 10 seconds, then heap dump";
  }
}




@Slf4j
@Service
class Worker {
//  @Async // runs on a default Spring thread pool with 8 threads
  public void processFile(int taskId, Path contents) {
    log.debug("Start task {}...", taskId);
    sleepMillis(10_000);
//    try (FileInputStream fromTemp = new FileInputStream(contents.toFile())) {
//      fromTemp.re
//    } finally {
//    contents.toFile().delete();
//    }
    log.debug("Done task");
    // delete temp file
  }
}

/**
 * KEY POINTS
 * - Don't pass large objects as params to async methods
 * - Tune the queue size of the underlying thread pool considering the size of elements
 */