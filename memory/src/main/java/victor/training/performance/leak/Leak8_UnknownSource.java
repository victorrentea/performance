package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big20MB;

@RestController
public class Leak8_UnknownSource {
   @GetMapping("leak8")
   public String endpoint() {
     Big20MB big = new Big20MB();
     return """
             Does this leak memory?
             Tip: record stack traces of allocation of suspect objects in the heap.
             """ + big;

      // TODO dar daca alocai si tu in alt flow (noise), dar din unele locuri ce se aloca ramane alive?
     // poti pune mana pe niste obiecte ramase de mult si intreba: cine le-a alocat pe alea ? (pecestcak?)
   }
}
/**
 * ⭐️ KEY POINTS
 * ☣️ Most leaks occur in libraries or unknown code
 * 🧠 Profilers (visualVM and JFR) can record stack traces of allocation places
 */
