package victor.training.performance.leak;

import jakarta.annotation.PostConstruct;
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
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak5/upload")
public class Leak5_Upload {
  @Autowired
  private Worker worker;
  AtomicInteger taskIndex = new AtomicInteger(0);

  @PostMapping
  public int endpoint(@RequestParam MultipartFile file) throws IOException {
    byte[] contents = file.getBytes();
    int taskId = taskIndex.incrementAndGet();
    worker.processFile(taskId, contents);
    return taskId;
  }
}

@Slf4j
@Service
class Worker {
  @Async // runs on a thread pool with 8 threads, by default in Spring
  public void processFile(int taskId, byte[] contents) {
    log.debug("Task {} start ...", taskId);
    sleepMillis(10_000);
    String contentsString = new String(contents);
//    long count = IntStream.range(0, contents.length).filter(i -> contents[i] == 17).count();
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