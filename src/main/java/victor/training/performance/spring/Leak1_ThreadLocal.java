package victor.training.performance.spring;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;
@Scope(value = "request",proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
@Data
class RequestData {
   private BigObject20MB bigObject;
}

@Slf4j
@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal implements CommandLineRunner {
   public static final ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();
   @Autowired
   RequestData requestData;

   @GetMapping
   public String test() {
      BigObject20MB bigObject = new BigObject20MB();
      threadLocal.set(bigObject);
      requestData.setBigObject(bigObject);

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

   @Override
   public void run(String... args) throws Exception {
      for (ClassLoader loader = Leak1_ThreadLocal.class.getClassLoader();
           loader != null;
           loader = loader.getParent()) {

         log.debug("Loader: " + loader);
      }
   }
}



