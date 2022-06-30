package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static victor.training.performance.util.PerformanceUtil.sleepq;

@Slf4j
public class ThreadLocals {
    public static void main(String[] args) {
        System.out.println("Here come 2 parallel HTTP requests");
        ThreadLocals app = new ThreadLocals();
        // ThreadLocals unrely these things that you did not understand how they work yet:
        // SecurityContextHolder,
        // how @Transactional magically propagates to other methods called inside,
        // Apache Sleuth,
        // Logback MDC,
        // why you cannot use the same JDBC conenction on 2 threads, @Scope("request")

        new Thread(() ->app.anHttpRequest("alice", "Alice's data")).start();
        new Thread(() ->app.anHttpRequest("bob", "Bob's data")).start();
    }

    private final AController controller = new AController(new AService(new ARepo()));

    public static ThreadLocal<String> staticCurrentUser = new ThreadLocal<>();

    // a thread local variable is a primitive in HJava that associates some data with the current thread.
//    Only this thread is able to read/write that value.
    // TODO ThreadLocal<String>

    public void anHttpRequest(String currentUser, String data) {
        log.info("Current user is " + currentUser);
        //the security filters before the controller they know the current uyser
        staticCurrentUser.set(currentUser);
        // TODO pass the current user down to the repo without polluting all signatures
        controller.create(data);
    }
}

@RequiredArgsConstructor
class AController {
    private final AService aService;
    public void create(String data) {
        aService.create(data);
    }
}
@RequiredArgsConstructor
class AService {
    private final ARepo aRepo;
    public void create(String data) {
        sleepq(10); // some delay, to reproduce the race bug
        aRepo.save(data);
    }
}
@Slf4j
class ARepo {
    public void save(String data) {
        String currentUser = ThreadLocals.staticCurrentUser.get(); // TODO How to get this?
        log.info("INSERT INTO A(data, created_by) VALUES ({}, {})", data, currentUser);
    }
}
