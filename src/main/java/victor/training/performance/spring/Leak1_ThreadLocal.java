package victor.training.performance.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal {
   public static ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();

   @GetMapping
   public String test() {
      BigObject20MB bigObject = new BigObject20MB();
      threadLocal.set(bigObject);
      try {
         businessMethod1();
      } finally {
         threadLocal.remove(); // best practice mereu in finally imediat dupa set.
      }
      return "Magic can do harm.";
   }

   // nu te loveste leakul in sine (e mic de obiceiL un string)
   // ci buguri, ca datele raman pe threadul pe care-l primeste alt request ulterior.
   // SI ta-naaaa, ai IP-ul requestului precedent.

   // !NotA: acest leak de thread localuri apare doar in combinatie cu threadpool.

   private void businessMethod1() {
      businessMethod2();
   }

   private void businessMethod2() {
      BigObject20MB bigObject = threadLocal.get();
      System.out.println("Business logic using " + bigObject);
      // TODO think of throw new RuntimeException();
   }
}


