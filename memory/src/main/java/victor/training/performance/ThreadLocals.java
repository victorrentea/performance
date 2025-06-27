package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ThreadLocals {
    private final AController controller = new AController(new AService(new ARepo()));
    public static void main(String[] args) throws InterruptedException {
        ThreadLocals app = new ThreadLocals();
        System.out.println("Imagine incoming HTTP requests...");
        CompletableFuture.runAsync(()->app.httpRequest("alice", "alice's data"));
        CompletableFuture.runAsync(()->app.httpRequest("bob", "bob's data"));
        Thread.sleep(1000);
    }

    public void httpRequest(String currentUser, String data) {
        log.info("Current user is " + currentUser);
        controller.create(data,currentUser);
    }

    // variabila globala din care fiecare thread isi vede doar propria valoare.
    // nu poti sa ai race-uri.
    // usecase: security context, user session, TraceID = metadate
    public static ThreadLocal<String> staticCurrentUser = new ThreadLocal<>();
}
// ---------- end of framework -----------

// ---------- Controller -----------
@RestController
@RequiredArgsConstructor
class AController {
    private final AService service;

    public void create(String data, String user) {
        ThreadLocals.staticCurrentUser.set(user);
        service.create(data);
    }
}

// ----------- Service ------------
@Service
@RequiredArgsConstructor
class AService {
    private final ARepo repo;

    public void create(String data) {
        sleepMillis(10); // some delay, to reproduce the race bug
        repo.save(data);
    }
}

// ----------- Repository ------------
@Repository
@Slf4j
class ARepo {
    public void save(String data) {
        String currentUser = ThreadLocals.staticCurrentUser.get();
        log.info("INSERT INTO A (data={}, created_by={}) ", data, currentUser);
    }
}
