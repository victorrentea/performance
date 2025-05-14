package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

@Slf4j
public class ParallelStreams {
  public static void main(String[] args) throws ExecutionException, InterruptedException {
//     OnAServer.otherParallelRequestsAreRunning(); // starve the shared commonPool din JVM

    List<Integer> list = IntStream.range(1, 100).boxed().toList();

    //to warm
    list.parallelStream().filter(i -> i % 2 == 0).map(ParallelStreams::apiCall).toList();

    long t0 = System.currentTimeMillis();

    // spread works on ForkFoinPool.commonPool() .size= #cpu-1 + main = #cpu
    var result = list.parallelStream()
        .filter(i -> i % 2 == 0)
        .map(i -> apiCall(i))
        .toList();

    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms to get: {}", t1 - t0, result);
  }

  private static int apiCall(Integer i) {
    log.debug("Map " + i);
    //sleepMillis(100); // network call (DB, REST, SOAP..) or CPU work
    // call GET http://localhost:9999/100ms with a new RestTemplate instance
    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:9999/100ms";
    String result = restTemplate.getForObject(url, String.class);
    return i * 2;
  }
}
