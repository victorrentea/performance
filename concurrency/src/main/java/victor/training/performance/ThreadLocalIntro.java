package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;
import static victor.training.performance.ThreadLocalIntro.staticCurrentUser;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ThreadLocalIntro {
    private final AController controller = new AController(new AService(new ARepo()));
    public static void main(String[] args) {
        ThreadLocalIntro app = new ThreadLocalIntro();
        System.out.println("Imagine incoming HTTP requests...");
        runAsync(() -> app.httpRequest("alice", "alice's data"));
        runAsync(() -> app.httpRequest("bob", "bob's data"));
        sleepMillis(100);
    }

    public void httpRequest(String currentUser, String data) {
//        HttpServletRequest r;
//        r.getSession().getAttribute("username")
        log.info("Current user is " + currentUser);
        staticCurrentUser.set(currentUser);
        controller.create(data);
    }
    // in variabila asta ce scrie/citeste un thread doar acel thread vede
    public static final ThreadLocal<String> staticCurrentUser = new ThreadLocal<>();
    // use-cases:
    // - metadate de user de pe request: username, httpRequestTimestamp, IP, language, timezone
    // - rolurile userului -> SecurityContextHolder, pt @Secured/@RolesAllowed/@PreAuthorize, JWT Token
    // - Logback MDC (Mapped Diagnostic Context) -> pt a pune in log fiecare log line cu userul curent (eg)
    // - TraceID + OTEL "Baggage"<<
    // - @Transactional propaga tx curenta catre toate metodele chemate in threadul tau
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
        String currentUser = staticCurrentUser.get();
        log.info("INSERT INTO A (data={}, created_by={}) ", data, currentUser);
    }
}
