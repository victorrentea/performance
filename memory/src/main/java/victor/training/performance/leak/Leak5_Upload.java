package victor.training.performance.leak;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("leak5/upload")
public class Leak5_Upload {
  @Autowired
  private Worker worker;
  AtomicInteger taskIndex = new AtomicInteger(0);

  @PostMapping
  public int endpoint(@RequestParam MultipartFile file) throws IOException, ExecutionException, InterruptedException {
    log.info("Got file");
    int taskId = taskIndex.incrementAndGet();
    // store the file in a temporary file

    // add timestamp to delete older than 24h automatically
    File tempFile = File.createTempFile("leak5" + LocalDateTime.now() + "-", ".tmp");
//    byte[] contents = file.getBytes();
//    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//      fos.write(contents);
//    }
    var uploadByteStream = file.getInputStream();
    Files.copy(uploadByteStream, tempFile.toPath());

    worker.processFile(taskId, tempFile);
    return taskId;
  }
}

@Slf4j
@Service
class Worker {
  @SneakyThrows
  @Async // runs on a thread pool with 8 threads, by default in Spring
  public void processFile(int taskId, File file) {
    byte[] contents = Files.readAllBytes(file.toPath());
    log.debug("Task {} start ...", taskId);
//    if (true) throw new RuntimeException("What if BUG");
    sleepMillis(10_000);
    String contentsString = new String(contents);
    long count = contentsString.chars().filter(c -> c == 17).count();
    log.debug("Task {} done = {}", taskId, count);
  }
}


// ======= supporting code =========
@RestController
@RequestMapping("leak5")
class Leak5_Tester {
  @Autowired
  private RestTemplate restTemplate;
  private final File file = new File("file-to-upload.txt");
  @PostConstruct
  public void generateFile() throws IOException {
    try (FileWriter writer = new FileWriter(file)) {
      for (int i = 0; i < 1024; i++) {
        writer.write("a".repeat(1024)+"\n");
      }
    }
  }

  @GetMapping
  public String home() {
    return """
        Upload a 1 MB file:<br>
        <ol>
        <li><a href='/leak5/test'>using this link</a> or
        <li>
          <form action='/leak5/upload' method='post' enctype='multipart/form-data' style='display:inline'>
          File:<input type='file' name='file' /> <input type='submit' value='Upload' />
          </form>
        <li>curl -F "file=@%s" http://localhost:8080/leak5/upload
        </ol>
        Do this 20 times within 10 seconds, then study the heap dump
        """.formatted(file.getAbsolutePath());
  }

  @GetMapping("test")
  public String test() throws IOException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", new FileSystemResource(file));
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

    int taskId = restTemplate.postForEntity("http://localhost:8080/leak5/upload", request, Integer.class).getBody();
    return "1MB uploaded. task.id=" + taskId;
  }
}


/**
 * KEY POINTS
 * - Don't pass large objects as params to async methods
 * - Tune the queue size of the underlying thread pool considering the size of elements
 */