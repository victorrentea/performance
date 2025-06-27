package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak8")
public class Leak8_UnknownSource {
   @GetMapping
   public String endpoint() {
      return "Nothing fishy here. Find the leak! " +
             "Tip: Record Allocation Profile via jvisualVM " +
             "or JFR (record stack traces of constructor calls for target classes)";
   }
}
/**
 * KEY POINTS
 * - Most leaks and tough performance issues occur in libraries or unknown code.
 * - Profilers (visualVM and JFR) can record stack traces of allocation places (new)
 */
