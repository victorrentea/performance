package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak13")
public class Leak13_Deadlock_Pro {
  private static final Map<String, String> mapA = Collections.synchronizedMap(new HashMap<>());
  private static final Map<String, String> mapB = Collections.synchronizedMap(new HashMap<>());

  @GetMapping("/one")
  public String one(@RequestParam(defaultValue = "a") String a) throws Exception {
    mapA.compute(a, (k, v) -> (v == null ? "" : v) + process(a) + mapB.get(a));
    return """
        Refresh the page, then call <a href='two'>/two</a> within 3 seconds to produce the deadlock.<br>
        Effect: requests in both tabs hang.""";
  }

  @GetMapping("/two")
  public String two(@RequestParam(defaultValue = "a") String a) throws Exception {
    mapB.compute(a, (k, v) -> (v == null ? "" : v) + process(a) + mapA.get(a));
    return """
        Refresh the page, then call <a href='one'>/one</a> within 3 seconds to produce the deadlock.<br>
        Effect: requests in both tabs hang.""";
  }

  private String process(String a) {
    sleepMillis(3000);
    return "🤔";
  }

  @GetMapping
  public String home() {
    return "call <a href='./one'>one</a> and <a href='./two'>two</a> within 3 secs..";
  }
}


/**
 * KEY POINTS
 * - avoid passing lambdas to synchonized methods
 * - Bad Design: bidirectional coupling: A->B->A !
 */
