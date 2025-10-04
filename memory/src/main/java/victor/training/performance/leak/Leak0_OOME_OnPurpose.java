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

    // TODO
    //  - add JVM arg: -XX:+HeapDumpOnOutOfMemoryError
    //  - add JVM arg: -XX:HeapDumpPath=/path/to/folder
    //  ± add JVM arg: -XX:+ExitOnOutOfMemoryError
    //  - Open in browser http://localhost:8080/leak0
    //  - Find the .hprof file in /path/to/folder
    //  - Open it in IntelliJ, VisualVM, jProfiler or MemoryAnalyzer
    //  - Experiment: turn variable into field
  }
}
/** ⭐️ KEY POINTS
 * 👍 Check that OOME in Prod kills instance + you can get the heapdump.hprof
 */
