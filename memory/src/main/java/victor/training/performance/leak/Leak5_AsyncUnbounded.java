package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.h2.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak5")
@Slf4j
public class Leak5_AsyncUnbounded {
  @Autowired
  private Worker worker;
  private static final AtomicInteger index = new AtomicInteger(0);

  @PostMapping
  public String endpoint(
      @RequestParam(value = "file", required = false)
      MultipartFile multipartFile) throws IOException {
//    byte[] contents = multipartFile.getBytes(); // Vulnerability: too large, potentiall OOME

    log.info("Received file");
    var file = Files.createTempFile("leak5", ".txt");
    try (var os = Files.newOutputStream(file)) {
      IOUtils.copy(multipartFile.getInputStream(), os);
    }

    worker.processFile(index.incrementAndGet(), file);
    // more robust against shutdown is to INSERT a row in FILES_TO_PROCESS table in DB the job to process in a DB and have
    // 1) file in CLOB/BLOB (the age of DB)
    // 2) file path on a NAS!! that temp folder is now shared among all instances
    // a @Scheduled(rate=10s) method that picks up the jobs and processes them from DB.

    return "Keep calling this 20 times within 10 seconds, then heap dump";
  }
}
@Slf4j
@Service
class Worker {
  @Async // runs on a default Spring thread pool with 8 threads
  // with an unbounded work queue
  // 1) limit the queue by defining a custom Executor and setting the queue size => reject
  // 2) don't pass LARGE objects to @Async methods (keep in memory)
  public void processFile(int taskId, Path contents) {
    log.debug("Start task {}...", taskId);
    sleepMillis(10_000);
    try (var is = Files.newInputStream(contents)) {
      // TODO ...
//      byte[] bytes = IOUtils.readBytesAndClose(is, -1);
//      log.debug("Read {} bytes", bytes.length);
//      int count = (int) IntStream.range(0, contents.length).filter(i -> contents[i] == 17).count();
    } catch (IOException e) {
      log.error("Failed to read file", e);
    }
    log.debug("Done task");
    ///////// HERE: please delete the temp file in a finally block
    // #2 contents.toFile().deleteOnExit();
    // #3 have a Quarterly cleanuop job deleteing old files
  }
}

/**
 * KEY POINTS
 * - Don't pass large objects as params to async methods
 * - Tune the queue size of the underlying thread pool considering the size of elements
 */