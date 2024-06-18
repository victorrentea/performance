package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ThreadLocalIntro {
    private final AController controller = new AController(new AService(new ARepo()));
    public static void main(String[] args) {
        ThreadLocalIntro app = new ThreadLocalIntro();
        System.out.println("Imagine incoming HTTP requests...");
        app.httpRequest("alice", "alice's data");
    }

    public void httpRequest(String currentUser, String data) {
//        HttpServletRequest r;
//        r.getSession().getAttribute("username")
        log.info("Current user is " + currentUser);
        controller.create(data, currentUser);
    }
    public static String staticCurrentUser;
}
// ---------- end of framework -----------

// ---------- Controller -----------
@RestController
@RequiredArgsConstructor
class AController {
    private final AService service;

    public void create(String data, String user) {
        service.create(data, user);
    }
}

// ----------- Service ------------
@Service
@RequiredArgsConstructor
class AService {
    private final ARepo repo;

    public void create(String data, String user) {
        sleepMillis(10); // some delay, to reproduce the race bug
        repo.save(data, user);
    }
}

// ----------- Repository ------------
@Repository
@Slf4j
class ARepo {
    public void save(String data, String user) {
        String currentUser = user;
        log.info("INSERT INTO A (data={}, created_by={}) ", data, currentUser);
    }
}
