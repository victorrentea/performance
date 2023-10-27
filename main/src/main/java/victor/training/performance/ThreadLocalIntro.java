package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static victor.training.performance.ThreadLocalIntro.staticCurrentUser;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ThreadLocalIntro {
    private final AController controller = new AController(new AService(new ARepo()));
    public static void main(String[] args) {
        ThreadLocalIntro app = new ThreadLocalIntro();
        System.out.println("Imagine incoming HTTP requests...");
        CompletableFuture.runAsync(()->app.httpRequest("alice", "alice's data"));
        CompletableFuture.runAsync(()->app.httpRequest("bob", "bob's data"));
        System.out.println("DONE: but my program dies because by default CF run on FJP.commonPool() which is a daemon pool");
        // and a daemon thread does not prevent the JVM from exiting
        sleepMillis(2000);
    }

    public void httpRequest(String currentUser, String data) {
        log.info("Current user is " + currentUser);
        staticCurrentUser.set(currentUser);
        controller.create(data);
    }
    public static ThreadLocal<String> staticCurrentUser = new ThreadLocal<>(); // huh!?
    // ThreadLocal is a magic variable that is different for each thread
    // therefore it is safe to use it in a multi-threaded environment
    // MDC
    // TraceId
    // @Transactional, JDBC connection
    // SecurityContextHolder
    // @Scope("request" or "session")
    // @RequestScope = CDI

    // THread locals DONT WORK In REACTIVE CHAINS. THE ONLY ALTERNATIVE is REACTOR CONTEXT + operator lifter.
    // It is possible to move metadaata via reactor context upstream the chain and offload it on ThreadLocals
    // by writing a form of AOP over operators = Operator Lifter see Branch: master on git: https://github.com/victorrentea/reactive.git search for "Lifter"
}
// ---------- end of framework -----------

// ---------- Controller -----------
//@RestController
@RequiredArgsConstructor
class AController {
    private final AService service;

    public void create(String data) {
        service.create(data);
    }
}

// ----------- Service ------------
//@Service
@RequiredArgsConstructor
class AService {
    private final ARepo repo;

    public void create(String data) {
        sleepMillis(10); // some delay, to reproduce the race bug
        repo.save(data);
    }
}

// ----------- Repository ------------
//@Repository
@Slf4j
class ARepo {
    public void save(String data) {
        String currentUser = staticCurrentUser.get(); // TODO
        log.info("INSERT INTO A(data, created_by) VALUES ({}, {})", data, currentUser);
    }
}
