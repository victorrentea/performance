package victor.training.performance.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak8")
public class Leak8_UnknownSource {
   @GetMapping
   public String test() {
      return "Nothing fishy here. Find the leak! Tip: Allocation Profile (record stack traces of constructor calls for target classes)";
   }
}
