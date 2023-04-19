package victor.training.performance;

import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ThreadLocalIntro {
    public static void main(String[] args) {
        System.out.println("Here come 2 parallel HTTP requests");
        ThreadLocalIntro app = new ThreadLocalIntro();
        app.httpRequest("alice", "Alice's data");
//        app.frameworkReceivesRequest("bob", "Bob's data");
    }

    private final AController controller = new AController(new AService(new ARepo()));

    public static String staticCurrentUser;
      public static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();
      // This is how the following works:
    // @Transactional, Logback MDC, Sleuth TraceID, SecurityContextHolder, @Scope("request"), current HTTP request

    // inside Spring, JavaEE,..
    public void httpRequest(String currentUser, String data) {
        log.info("Current user is " + currentUser);
        usernameHolder.set(currentUser);
//        staticCurrentUser = currentUser;
        // TODO pass the current user down to the repo WITHOUT polluting all signatures
        controller.create(data);
    }
}


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
class ARepo {
    public void save(String data) {
        ThreadLocalIntro.usernameHolder.get();
        String currentUser = ThreadLocalIntro.usernameHolder.get();//"TODO"; // TODO Where to get this from?
        log.info("INSERT INTO A(data, created_by) VALUES ({}, {})", data, currentUser);
    }
}
