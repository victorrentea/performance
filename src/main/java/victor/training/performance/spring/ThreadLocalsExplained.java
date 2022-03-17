package victor.training.performance.spring;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static victor.training.performance.util.PerformanceUtil.sleepq;

@SpringBootApplication
public class ThreadLocalsExplained {
   public static void main(String[] args) {
       SpringApplication.run(ThreadLocalsExplained.class, args);
   }
//   public static void main(String[] args) {
//      UnController controller = new UnController(new UnService(new UnRepo()));
//      new Thread(() -> {controller.laIntrare("ion");}).start();
//      new Thread(() -> {controller.laIntrare("maria");}).start();
//   }
}

@Scope(value = "request",proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
@Data
class UsernameHolderCuSpring {
   private String username;
}

class UsernameHolder {
   private static ThreadLocal<String> currentUsername  = ThreadLocal.withInitial(() -> "anonymous");

   public static String getCurrentUsername() {
      return currentUsername.get();
   }

   public static void setCurrentUsername(String currentUsername) {
//    1) pasa metadate
           // a) SecurityContextHolder.getContext().getAuthentication().getName();
           // b) @Scope("request") @Component
      // 2) Thread safety: pe o singura conexiune JDBC vreau sa inser din 2 threaduri.
          // IMPOSIBIL. JDBCConnection isi tine toata starea pe un thread local.
         // pentru ca au vrut sa NU PERMITA race bugs pe starea conexiuni

      UsernameHolder.currentUsername.set(currentUsername);
   }
}
@RestController
@Slf4j
@RequiredArgsConstructor
class UnController {
   private final UnService unService;
   private final UsernameHolderCuSpring usernameHolderCuSpring;

   @GetMapping("request-scope")
   public void laIntrare(@RequestParam String username) { // sau in vreun filtru magic de security
//      UsernameHolder.setCurrentUsername(username);
      usernameHolderCuSpring.setUsername(username);
      log.info("Sunt userul: " + username);
      unService.met();
   }
}
@Service
@RequiredArgsConstructor
class UnService {
   private final UnRepo unRepo;

   public void met() {
      sleepq(5_000);
      unRepo.update();
   }
}

@RequiredArgsConstructor
@Repository
@Slf4j
class UnRepo {
   private final UsernameHolderCuSpring usernameHolderCuSpring;

   @SneakyThrows
   public void update() {
      String username = usernameHolderCuSpring.getUsername();

      CompletableFuture.runAsync(() -> {

         log.info("UPDATE ... SET LAST_MODIFIED_BY=?   (de catre " + username);
      }).get();
   }
}