package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ThreadLocalIntro {
    private final AController controller = new AController(new AService(new ARepo()));
    public static void main(String[] args) throws InterruptedException {
        ThreadLocalIntro app = new ThreadLocalIntro();
        System.out.println("Imagine incoming HTTP requests...");
//        app.httpRequest("alice", "alice's data");
        CompletableFuture.runAsync(()->app.httpRequest("alice", "alice's data"));
        CompletableFuture.runAsync(()->app.httpRequest("bob", "bob's data"));
        // the threads running my tasks are from ForkJoinPool.commonPool()
        // which threads are by default daemon threads (not keeping the JVM alive)
        Thread.sleep(1000);
    }

    public void httpRequest(String currentUser, String data) {
        log.info("Current user is " + currentUser);
        staticCurrentUser.set(currentUser);
        controller.create(data);
    }

    //despite the fact that it's still a SINGLE INSTANCE ON HEAP reference via a single static variable,
    // each thread has its own value for it.
    // Used by:
    // - JDBC Connection ± @Transactional
    // - TraceID
    // - SecurityContextHolder (Spring)
    public static ThreadLocal<String> staticCurrentUser = new ThreadLocal<>();
}
// ---------- end of framework -----------

// ---------- Controller -----------
@RestController
@RequiredArgsConstructor
class AController {
    private final AService service;

    public void create(String data) {
        service.create(data);
    }
}

@Service
@RequiredArgsConstructor
class AService {
    private final ARepo repo;

    public void create(String data) {
        sleepMillis(10); // some delay, to reproduce the race bug
        repo.save(data);
    }
}

@Repository
@Slf4j
class ARepo {
    public void save(String data) {
        String currentUser = ThreadLocalIntro.staticCurrentUser.get(); //hocus pocus, get the current user from the framework
        log.info("INSERT INTO A (data={}, created_by={}) ", data, currentUser);
    }
}
