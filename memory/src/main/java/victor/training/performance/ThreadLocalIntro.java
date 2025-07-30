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
    public static void main(String[] args) {
        ThreadLocalIntro app = new ThreadLocalIntro();
        System.out.println("Imagine incoming HTTP requests...");
        CompletableFuture.runAsync(() -> app.httpRequest("alice", "alice's data"));
        CompletableFuture.runAsync(() -> app.httpRequest("bob", "bob's data"));
        sleepMillis(1000);
    }

    public void httpRequest(String currentUser, String data) {
        log.info("Current user is " + currentUser);
        staticCurrentUser = currentUser;
        controller.create(data);
    }
    public static String staticCurrentUser;
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
        String currentUser = ThreadLocalIntro.staticCurrentUser;
        log.info("INSERT INTO A (data={}, created_by={}) ", data, currentUser);
    }
}
