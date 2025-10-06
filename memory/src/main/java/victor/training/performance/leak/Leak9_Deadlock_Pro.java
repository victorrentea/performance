package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big1KB;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("leak9")
public class Leak9_Deadlock_Pro {
  private static final Map<String, String> mapA = Collections.synchronizedMap(new HashMap<>());
  private static final Map<String, String> mapB = Collections.synchronizedMap(new HashMap<>());

  @GetMapping("/one")
  public String one(@RequestParam(defaultValue = "a") String a) {
    Big1KB obj = new Big1KB();
    log.info("Start one with {} on stack", obj);
    mapA.compute(a, (k, v) -> (v == null ? "" : v) + process(a) + mapB.get(a));
    return howtoDeadlockInstructions("two");
  }

  @GetMapping("/two")
  public String two(@RequestParam(defaultValue = "a") String a) {
    log.info("Start two");
    mapB.compute(a, (k, v) -> (v == null ? "" : v) + process(a) + mapA.get(a));
    return howtoDeadlockInstructions("one");
  }

  //TODO take a thread (core) dump ± Virtual Threads
  private String process(String a) {
    sleepMillis(3000);
    return "🤔";
  }

// === === === === === === === Support code  === === === === === === ===

  @GetMapping
  public String home() {
    return "call <a href='/leak9/one'>one</a> and <a href='/leak9/two'>two</a> within 3 secs..";
  }

  private String howtoDeadlockInstructions(String other) {
    return """
        Refresh the page, then <a href='%s' target='_blank'>call /%s</a> 
        within 3 seconds to produce the deadlock.<br>
        Expected Effect: both tabs hang (never finish loading).<br>
        Extra fun: load test it then go to homepage<br>
        What if I use Virtual Threads?""".formatted(other,other);
  }
}


/**
 * ⭐️ KEY POINTS
 * 👍 avoid passing lambdas to synchronized methods
 */
