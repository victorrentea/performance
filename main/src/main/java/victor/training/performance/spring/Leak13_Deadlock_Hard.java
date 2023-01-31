package victor.training.performance.spring;

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
public class Leak13_Deadlock_Hard {
  // [RO] CATE DOI, CATE DOI ... : https://youtu.be/V798MhKfdZ8

  private static final Map<String, String> mapA = Collections.synchronizedMap(new HashMap<>());
  private static final Map<String, String> mapB = Collections.synchronizedMap(new HashMap<>());

  @GetMapping
  public String root() {
    return "call <a href='./leak13/one'>one</a>" +
		   " and <a href='./leak13/two' >two</a> withing 3 secs..";
  }

  @GetMapping("/one")
  public String one(@RequestParam(defaultValue = "a") String a) throws Exception {
    mapA.compute(a, (k, v) -> (v == null ? "" : v) + f() + mapB.get(a));
    return "--> You didn't call /two within the last 3 secs, didn't you?..";
  }

  private String f() {
    sleepMillis(3000);
    return "🤔";
  }

  @GetMapping("/two")
  public String two(@RequestParam(defaultValue = "a") String a) throws Exception {
    mapB.compute(a, (k, v) -> (v == null ? "" : v) + f() + mapA.get(a));
    return "--> You didn't call /one within the last 3 secs, didn't you?..";
  }
}


/**
 * KEY POINTS
 * - avoid passing lambdas to synchonized methods
 * - Bad Design: bidirectional coupling: A->B->A !
 */
