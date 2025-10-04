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
@RequestMapping("leak13")
public class Leak13_Deadlock_Pro {
  private static final Map<String, String> mapA = Collections.synchronizedMap(new HashMap<>());
  private static final Map<String, String> mapB = Collections.synchronizedMap(new HashMap<>());

  @GetMapping("/one")
  public String one(@RequestParam(defaultValue = "a") String a) throws Exception {
    Big1KB obj = new Big1KB();
    log.info("Start one with " + obj);
    mapA.compute(a, (k, v) ->
        (v == null ? "" : v) + process(a) + mapB.get(a));
    return printHowtoMessage("two");
  }

  @GetMapping("/two")
  public String two(@RequestParam(defaultValue = "a") String a) throws Exception {
    log.info("Start two");
    mapB.compute(a, (k, v) ->
        (v == null ? "" : v) + process(a) + mapA.get(a));
    return printHowtoMessage("one");
  }

  private String printHowtoMessage(String other) {
    return """
        Refresh the page, then <a href='%s' target='_blank'>call /%s</a> 
        within 3 seconds to produce the deadlock.<br>
        Expected Effect: both tabs hang (never finish loading).""".formatted(other,other);
  }
  //TODO dump threads (test inIntelliJ)

  private String process(String a) {
    sleepMillis(3000);
    return "🤔";
  }

  @GetMapping
  public String home() {
    return "call <a href='/leak13/one'>one</a> and <a href='/leak13/two'>two</a> within 3 secs..";
  }
}


/**
 * KEY POINTS
 * - avoid passing lambdas to synchonized methods
 * - Bad Design: bidirectional coupling: A->B->A !
 */
