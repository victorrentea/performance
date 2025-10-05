package victor.training.performance.leak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

import static victor.training.performance.util.PerformanceUtil.measureAllocation;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
public class Leak1Lib_ThreadLocalCache {
  @GetMapping("leak1/lib")
  public String endpoint() {
    String work = Library.method();
    sleepMillis(1000); // application logic
    return "Manifests under high RPS on Virtual Threads";
  }
}

/** ⭐️ KEY POINTS
 * ☢️ ThreadLocal data can make Virtual Threads heavy again
 */

// --- Library code I cannot change 🔽 ---
class Library {
  private static final Logger log = LoggerFactory.getLogger(Library.class);

  public static String method() {
    return "A bit of work using " + getCachedLifeContext();
  }

  private static final ThreadLocal<LifeContext> threadLocal = new ThreadLocal<>();
  private static LifeContext getCachedLifeContext() {
  // JIRA-006 2010-03 Client threads probably return later as they are *probably* pooled
  //  => we will cache LifeContext in ThreadLocal
    if (threadLocal.get() != null) {
      return threadLocal.get();
    }
    LifeContext lifeContext = initLife();
    threadLocal.set(lifeContext);
    return lifeContext;
  }

  private static LifeContext initLife() {
    try {
      log.info("Searching for the Meaning of Life ...");
      sleepMillis(100); // takes time
      File pomXmlFile = new File("pom.xml");
      var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pomXmlFile);
      return new LifeContext(42, /*new Big20MB(),*/ document);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  static{
    var allocation = measureAllocation(() ->initLife());
    log.info("LifeContext.bytes = " + allocation.deltaHeapBytes());
  }

  private record LifeContext(
      int meaningOfLife,
      /*Big20MB knowledgeSchema,*/
      Document document) {}
}
