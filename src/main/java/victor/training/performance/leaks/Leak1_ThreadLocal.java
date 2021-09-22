package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal {
   public static ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();

//   @Transactional
//   @PreAuthorized
//   @RolesAllowed
   @GetMapping
   public String test() {
      BigObject20MB bigObject = new BigObject20MB();
      threadLocal.set(bigObject);

      try {
         businessMethod1();
      } finally {
         threadLocal.remove();
      }
      return "Magic can do harm.";
   }

   private void businessMethod1() {
      businessMethod2();
   }

   private void businessMethod2() {
      BigObject20MB bigObject = threadLocal.get();
      System.out.println("Business logic using " + bigObject);
      // TODO think of throw new RuntimeException();
   }
}


