package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ThreadLocalIntro {
    public static void main(String[] args) {
        System.out.println("Here come 2 parallel HTTP requests");
        ThreadLocalIntro app = new ThreadLocalIntro();
        new Thread(()->app.httpRequest("alice", "Alice's data")).start();
        new Thread(()->app.httpRequest("bob", "Bob's data")).start();
    }

    private final AController controller = new AController(new AService(new ARepo()));
// thread local este o varibila magica in care orice pun din threadul X doar threadul X vede datele.
    //
    public static ThreadLocal<String> staticCurrentUser=new ThreadLocal<>();
    // TODO ThreadLocal<String>


    // inside Spring, JavaEE,..
    public void httpRequest(String currentUser, String data) {
        log.info("Current user is " + currentUser);
        staticCurrentUser.set(currentUser);
        controller.create(data);
    }
    // 🥺 la ce ne trebuie magia asta ?

    // JDBC Connection
    // SecurityContextHolder (useru curent din spring)
    // @Transactional
    // @Scope("request/session")
    // Logback MDC ?!

}

// ===================================
// ---------- Controller -----------
@RestController
@RequiredArgsConstructor
class AController {
    private final AService aService;

    public void create(String data) {
        aService.create(data);
    }
}

// ----------- Service ------------
@Service
@RequiredArgsConstructor
class AService {
    private final ARepo aRepo;

    public void create(String data) {
        sleepMillis(10); // some delay, to reproduce the race bug
        aRepo.save(data);
    }
}

// ----------- Repository ------------
@Repository
@Slf4j
class ARepo { // the deepest place in my code.
    public void save(String data) {
//        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName;
        String currentUser = ThreadLocalIntro.staticCurrentUser.get(); // TODO Where to get this from?
        log.info("INSERT INTO A(data, created_by) VALUES ({}, {})", data, currentUser);
    }
}
