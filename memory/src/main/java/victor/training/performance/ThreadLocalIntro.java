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
        CompletableFuture.runAsync(()->app.httpRequest("alice", "alice's data"));
        CompletableFuture.runAsync(()->app.httpRequest("bob", "bob's data"));
        Thread.sleep(1000); // preventt process from exiting
        // as how to fix this problem CompletableFuture submit to ForkJoin.commonPool ofcomposed of threads.daemon=true
    }

    public void httpRequest(String currentUser, String data) {
        log.info("Current user is " + currentUser);
        staticCurrentUser.set(currentUser);
        controller.create(data,currentUser);
    }
    public static ThreadLocal<String> staticCurrentUser = new ThreadLocal<>(); // static global variable
}
// ---------- end of framework -----------

// ---------- Controller -----------
@RestController
@RequiredArgsConstructor
class AController {
    private final AService service;

    public void create(String data, String currentUser) {
        service.create(data, currentUser);
    }
}

// ----------- Service ------------
@Service
@RequiredArgsConstructor
class AService {
    private final ARepo repo;

    public void create(String data, String currentUser) {
        sleepMillis(10); // some delay, to reproduce the race bug
        repo.save(data, currentUser);
    }
}

// ----------- Repository ------------
@Repository
@Slf4j
class ARepo {
    public void save(String data, String currentUser) {
        log.info("INSERT INTO A (data={}, created_by={}) ", data, ThreadLocalIntro.staticCurrentUser.get());
    }
}
