package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ThreadLocalIntro {
    // Thread Local data

//    ThreadLocal<String> dataThatIsAvailableOnlyForYOUR_thread =
     // SecurityContextHolder.getContext() <-- static how can
    // @Transactional
    // Logback MDC
    // Sleuth

    public static void main(String[] args) {
        System.out.println("Here come 2 parallel HTTP requests");
        ThreadLocalIntro app = new ThreadLocalIntro();
       new Thread(()->app.httpRequest("alice", "Alice's data")).start();
       new Thread(()->app.httpRequest("bob", "Bob's data")).start();
    }

    private final AController controller = new AController(new AService(new ARepo()));

    public static ThreadLocal<String> staticCurrentUser = new ThreadLocal<>();
    // TODO ThreadLocal<String>

    // inside Spring, JavaEE,..
    public void httpRequest(String currentUserHead, String data) {
        log.info("Current user is " + currentUserHead);
        staticCurrentUser.set(currentUserHead); // the value Thread-1 sets can be later read only by Thread-1
        // TODO pass the current user down to the repo WITHOUT polluting all signatures
        controller.create(data);
    }
}


// ---------- Controller -----------
@RestController
@RequiredArgsConstructor
class AController {
    private final AService aService;

    @GetMapping
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
        String currentUser = ThreadLocalIntro.staticCurrentUser.get(); // TODO Where to get this from?
        log.info("INSERT INTO A(data, created_by) VALUES ({}, {})", data, currentUser);
    }
}
