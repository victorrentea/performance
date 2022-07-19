package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

import static victor.training.performance.util.PerformanceUtil.sleepq;

@Slf4j
public class ThreadLocals {
    public static void main(String[] args) {
        System.out.println("Here come 2 parallel HTTP requests");
        ThreadLocals app = new ThreadLocals();
        new Thread(()->app.httpEndpoint("alice", "Alice's data")).start();
        new Thread(()->app.httpEndpoint("bob", "Bob's data")).start();
    }

    private final AController controller = new AController(new AService(new ARepo()));

    public static ThreadLocal<String> staticCurrentUser = new ThreadLocal<>(); //DOAMNE FERESTE
    // TODO ThreadLocal<String>

    public static String getCurrentUser() {
        return staticCurrentUser.get();
    }

    // framework
    public void httpEndpoint(String currentUser, String data) {
        log.info("Current user is " + currentUser); // cookie, AccesToken
        staticCurrentUser.set(currentUser);
        // TODO pass the current user down to the repo without polluting all signatures
        controller.create(data);
    }
}

// ----------- Controller ---------------
@RequiredArgsConstructor
//@RestController
class AController {
    private final AService aService;

    @GetMapping
    public void create(String data) {
        aService.create(data);
    }
}
// ----------- Service ---------------
@RequiredArgsConstructor
class AService {
    private final ARepo aRepo;
    public void create(String data) {
        sleepq(10); // some delay, to reproduce the race bug
        aRepo.save(data);
    }
}
// ----------- Repo ---------------
@Slf4j
class ARepo {
    public void save(String data) {
        String currentUser = ThreadLocals.getCurrentUser();// TODO How to get this?
        // pe bune asa faci in Spring
        // aplicatii pentru Thread Local:

//        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        // @Transactional
        // JDBC Connection
        // @Scope("request" sau "session")

        log.info("INSERT INTO A(data, created_by) VALUES ({}, {})", data, currentUser);
    }
}
