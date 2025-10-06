package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Leak0_OOME_OnPurpose {
  @GetMapping("leak0")
  public String causeOOME() {
    List<int[]> boom = new ArrayList<>();
    while (true) boom.add(new int[1000_000]);

    // TODO + in your prod
    //  + JVM arg: -Xmx500m
    //  > Experiment: turn variable into field
    //  + JVM arg: -XX:+ExitOnOutOfMemoryError
    //  + JVM arg: -XX:+HeapDumpOnOutOfMemoryError
    //  + JVM arg: -XX:HeapDumpPath=/Users/victorrentea/workspace/performance
    //  > Open in browser http://localhost:8080/leak0
    //  > Find the .hprof file in /path/to/folder
    //  > Load it in IntelliJ, VisualVM, jProfiler or MemoryAnalyzer(MAT)
  }
}
/** ⭐️ KEY POINTS
 * 👍 OOME should kill the process
 * 👍 OOME should dump the heap on disk
 * ⚠️ You can get that heapdump.hprof from production
 * 😎 Sanitize/Anonymize heapdump with https://github.com/paypal/heap-dump-tool
 * ☣️ Instance fields of singleton are permanent ≈ 'static'
 */
