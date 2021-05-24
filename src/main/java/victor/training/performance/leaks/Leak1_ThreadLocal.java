package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.*;
import java.io.IOException;

@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal {
   public static ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();

   @GetMapping
   public String test() {
      BigObject20MB bigObject = new BigObject20MB();
      threadLocal.set(bigObject);

      businessMethod1();
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


