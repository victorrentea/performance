package victor.training.performance.leaks;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Component
@Data
@Scope("request")
class UserData {
   private String currentUsername;
   private String currentTenant;
   private String currentTimezone;
   private String currentLocale;
   private String correlationId; // use "sleuth" instead over messages and http calls
}

@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal {
   public static ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();
   public static ThreadLocal<String> tenantThreadLocal = new ThreadLocal<>();

//   @Autowired
//   UserData userData;
   public static ThreadLocal<String> currentUser = new ThreadLocal<>();



//   @ServiceActivator
//   public void method(Message message) {
//
//   }
//   public void runWithTenant(Runnable stuffToDo, String tenantId) {
//      tenantThreadLocal.set(tenantId);
//      try {
//         stuffToDo.run();
//      } finally {
//         tenantThreadLocal.remove();
//      }
//   }


   @GetMapping
   public String test() {
//      userData.setCurrentUsername("Gigi");

      BigObject20MB bigObject = new BigObject20MB(); // configuraiton info, cached data
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

      System.out.println("INSERT INTO CREATED_BY=?" + currentUser.get());
//      System.out.println("INSERT INTO CREATED_BY=?" + userData.getCurrentUsername());
   }
}


