package victor.training.performance.leak;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.leak.Leak6_Helper.fetchData;
import static victor.training.performance.util.PerformanceUtil.MB;
import static victor.training.performance.util.PerformanceUtil.sleepSeconds;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Leak6_Files {
  private final FileProcessor processor;
  private static final AtomicInteger counter = new AtomicInteger(0);

  @GetMapping("leak6/download") // 🔥 Leak6Load.java
  public String download() {
    int taskId = counter.incrementAndGet();
    MDC.put("traceId", "#" + counter);
    String data = fetchData(MB(10));
    log.info("Got {} bytes", data.length());
    CompletableFuture.runAsync(() -> processor.process(data, taskId));
    return """
        Task (10s) submitted #%d<br>
        Data: %,d bytes<br>
        Look in <a href='http://localhost:8080/actuator/prometheus'>metrics</a> for 'fjp'"""
        .formatted(taskId, data.length());
  }
}

@Slf4j
@Service
class FileProcessor {
  public void process(String contents, int taskId) {
    log.debug("Task {} started ...", taskId);
    //if (true) throw new RuntimeException("Bug🪲");
    sleepSeconds(10);
    long newLinesCount = contents.chars().filter(c -> c == 10).count();
    log.debug("Task {} completed: counted {} lines", taskId, newLinesCount);
  }
}


/**
 * ⭐️ KEY POINTS
 * 👍 Offload large objects from memory to (eg) disk/S3🪣
 * 👍 Use bounded queues (eg for thread pools)
 * ☣️ CompletableFuture.xyzAsync(->) run on FJP.commonPool(), which has an unbounded queue
 * 👍 ForkJoinPool.commonPool() can be monitored (see below how)
 * 👍 Pass a Spring executor to any CompletableFuture.*Async(,👉executor)
 */


// === === === === === === === Support code  === === === === === === ===

@Slf4j
@RestController
@RequiredArgsConstructor
class Leak6_FilesUpload {
  private static final AtomicInteger counter = new AtomicInteger(0);
  private final FileProcessor processor;

  @PostMapping("leak6/upload")
  public int upload(@RequestParam MultipartFile file) throws IOException {
    byte[] fileContents = file.getBytes();
    int taskId = counter.incrementAndGet();
    CompletableFuture.runAsync(() -> processor.process(new String(fileContents), taskId));
    return taskId;
  }
}

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
    executor.setQueueCapacity(5); // rejects tasks when the queue is full
    executor.setThreadNamePrefix("myexec-");
    executor.initialize();
    executor.setTaskDecorator(task -> {
      var submitterMDC = MDC.getCopyOfContextMap(); // de pe threadul părinte
      return () -> {
        try {
          MDC.setContextMap(submitterMDC);
          task.run();
        } finally {
          MDC.clear();
        }
      };
    });
    return executor;
  }
}


@RestController
@RequestMapping("leak6")
class Leak6_Helper {
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
        """.formatted(file.getAbsolutePath());
  }

  private static final String line = "lol".repeat(20) + "\n";

  @GetMapping(value = "file", produces = "text/plain")
  public void generateLargeFile(
      @RequestParam(required = false) Integer mb,
      @RequestParam(required = false) Integer kb,
      HttpServletResponse response) throws IOException {
    int bytes;
    if (kb != null) bytes = kb * 1024;
    else if (mb != null) bytes = mb * 1024 * 1024;
    else bytes = 10 * 1024 * 1024;
    int n = bytes / line.length();
    for (int i = 0; i < n; i++) {
      response.getWriter().write(line);
    }
  }

  public static String fetchData(int bytes) {
//    return RestClient.create()
//        .get().uri("http://localhost:8080/leak6/file?mb=10")
//        .retrieve()
//        .body(String.class);
    return dataFromMemory(bytes);
  }

  public static String dataFromMemory(int bytes) {
    StringWriter stringWriter = new StringWriter();
    int n = bytes / line.length();
    for (int i = 0; i < n; i++) {
      stringWriter.write(line);
    }
    return stringWriter.toString();
  }
}
