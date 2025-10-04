package victor.training.performance.leak;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Leak6_Files {
  private final FileProcessor processor;

  private static final AtomicInteger counter = new AtomicInteger(0);

  @PostMapping("leak6/upload")
  public int upload(@RequestParam MultipartFile file) throws IOException {
    byte[] fileContents = file.getBytes();
    log.info("Uploaded bytes: " + fileContents.length);
    // spring.servlet.multipart.max-file-size=30MB
    // spring.servlet.multipart.max-request-size=30MB
    return processAsync(new String(fileContents));
  }


  @GetMapping("leak6/download") // 🔥 Leak6Load.java
  public int download() {
    String fileContents = RestClient.create()
        .get().uri("http://localhost:8080/leak6/file?mb=10")
        .retrieve()
        .body(String.class);
    log.info("Downloaded bytes: " + fileContents.getBytes().length);
    return processAsync(fileContents);
  }

  private int processAsync(String fileContents) {
    int taskId = counter.incrementAndGet();
    CompletableFuture.runAsync(() -> processor.process(fileContents, taskId));
    log.info("Task submitted: " + taskId);
    return taskId;
  }
}

@Slf4j
@Service
class FileProcessor {
  public void process(String contents, int taskId) {
    log.debug("Task {} started ...", taskId);
    //if (true) throw new RuntimeException("Invisible Bug");
    sleepMillis(10_000);
    long newLinesCount = contents.chars().filter(c -> c == 10).count();
    log.debug("Task {} completed: lines = {}", taskId, newLinesCount);
  }
}

/**
 * ⭐️ KEY POINTS
 * ☣️ CompletableFuture.xyzAsync(->) uses an unbounded queue
 * 👍 Offload large objects from memory to (eg) disk/S3🪣
 * 👍 Use bounded queues for thread pools
 * 👍 ForkJoinPool.commonPool() can me monitored (see below)
 * 👍 Pass a Spring executor to any CompletableFuture.*Async(,executor)
 */


// === === === === === === === Support code  === === === === === === ===

@Configuration
class Leak6Config {
  @SuppressWarnings("resource")
  @Bean
  MeterBinder exposeMetrics_ofCommonForkJoinPool() {
    ForkJoinPool pool = ForkJoinPool.commonPool();
    return registry -> {
      Gauge.builder("fjp_cp__total_submitted", pool, ForkJoinPool::getStealCount).register(registry);
      Gauge.builder("fjp_cp__thread_pool_size", pool, ForkJoinPool::getPoolSize).register(registry);
      Gauge.builder("fjp_cp__queue_size", pool, p -> (double) p.getQueuedSubmissionCount()).register(registry);
    };
  }

  @Bean
  ThreadPoolTaskExecutor myExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(5); // bounded queue -> rejects tasks when full
    executor.setThreadNamePrefix("myexec-");
    executor.initialize();
    return executor;
  }
}


@RestController
@RequestMapping("leak6")
class Leak6_Tester {
  private final File file = new File("file-to-upload.txt");

  @PostConstruct
  public void generateFile() throws IOException {
    try (FileWriter writer = new FileWriter(file)) {
      for (int i = 0; i < 1024; i++) {
        writer.write("a".repeat(1024) + "\n");
      }
    }
  }

  @GetMapping
  public String home() {
    return """
        <h3>Upload a file to process</h3>
        <ol>
        <li>Paste in terminal<br> 
        curl -F "file=@%s" http://localhost:8080/leak6/upload
        <li>
          <form action='/leak6/upload' method='post' enctype='multipart/form-data' style='display:inline'>
          Or upload your own file:<input type='file' name='file' /> <input type='submit' value='Upload' />
          </form>
        </ol>
        
        <h3>Download a file on server to process</h3>
        <a href="/leak6/download">Click here</a>
        `<hr>
        Do this 20 times within 10 seconds, then study the heap dump
        """.formatted(file.getAbsolutePath());
  }

  @GetMapping(value = "file", produces = "text/plain")
  public void generateLargeFile(
      @RequestParam(value = "mb", required = false, defaultValue = "10") int sizeInMB,
      HttpServletResponse response) throws IOException {
    String line = "lol".repeat(10) + "\n";
    for (int i = 0; i < sizeInMB * 1024 * 1024 / line.length(); i++) {
      response.getWriter().write(line);
    }
  }
}
