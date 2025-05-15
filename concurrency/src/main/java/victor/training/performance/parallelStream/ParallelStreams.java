package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

@Slf4j
public class ParallelStreams {
  public static void main(String[] args) throws ExecutionException, InterruptedException {
     OnAServer.otherParallelRequestsAreRunning(); // starve the shared commonPool din JVM

    List<Integer> list = IntStream.range(1, 100).boxed().toList();

    //to warm
    list.parallelStream().filter(i -> i % 2 == 0).map(ParallelStreams::apiCall).toList();

    long t0 = System.currentTimeMillis();

    // spread works on ForkFoinPool.commonPool() .size= #cpu-1 + main = #cpu
    // > you can only hope improve
    // design intent of parallelStream() in Java Language. What should it be used for?
    // For HEAVY CPU work.
    // - IO might block others wanting to CPU
    // - tiny CPU work might lose performance for map/reduce overhead
//    var x = ContextSnapshot.captureAll();
    var result = list.parallelStream()
        .filter(i -> i % 2 == 0)
        .map(i -> apiCall(i))
//        .map(i -> x.wrap(()->apiCall(i)))
        // #bad practice = I/O in parallelStream().
        .toList();

    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms to get: {}", t1 - t0, result);
  }
  //@PreAuthorize
  //@Secured
  private static int apiCall(Integer i) {
    log.debug("Map " + i);
    //sleepMillis(100); // network call (DB, REST, SOAP..) or CPU work
    // call GET http://localhost:9999/100ms with a new RestTemplate instance
    String url = "http://localhost:9999/100ms";
    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(url, String.class);
    return i * 2;
  }
}
