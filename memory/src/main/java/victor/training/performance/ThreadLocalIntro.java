package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.symmetric.AES.CFB;
import org.slf4j.MDC;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ThreadLocalIntro {
    private final AController controller = new AController(new AService(new ARepo()));
    public static void main(String[] args) throws InterruptedException {
        ThreadLocalIntro app = new ThreadLocalIntro();
        System.out.println("Imagine incoming HTTP requests...");
        CompletableFuture.runAsync(() -> app.httpRequest("alice", "alice's data"));
        CompletableFuture.runAsync(() -> app.httpRequest("bob", "bob's data"));
        // by default -> ruleaza pe ForkJoinPool.commonPool() care are threadurile markage isDaemon=true
        // adica nu tine procesu din a muri.
        // procesu moare pana apuca sa ruleze alea.
        Thread.sleep(1000);
    }

    public void httpRequest(String currentUser, String data) {
        log.info("Current user is " + currentUser);
        staticCurrentUser.set(currentUser);
        controller.create(data);
    }
    // desi e statica, fiecare thread are propria valoare.
    // la nivel de CPU se foloseste TLS (Thread Local Storage) pentru a pastra aceasta valoare.
    public static ThreadLocal<String> staticCurrentUser = new ThreadLocal<>();
}
// ---------- end of framework -----------

// ---------- Controller -----------
@Slf4j
@RestController
@RequiredArgsConstructor
class AController {
    private final AService service;

    AtomicInteger batchId = new AtomicInteger(0);
    public /*synchronized */void create(String data) {
//        String username  = httpServletRequest.getSession().getAttribute("username");// anii 2000'
        MDC.put("batchId", batchId.incrementAndGet() % 100+"");
        log.info("Start");
        service.create(data);
        // Cerinta: toate logurile unui batch sa aiba un TOKEN comun in mesajul de log ca sa pot identifica tot ce a facut batch
    }
}
// ----------- Service ------------
@Service
@RequiredArgsConstructor
class AService {
    private final ARepo repo;

    public void create(String data) {
        sleepMillis(10); // some delay, to reproduce the race bug
        repo.save(data); // toate metodele din app mea vor sfarsi avand username param = GU-NOI!
    }
}

// ----------- Repository ------------
@Repository
@Slf4j
class ARepo {
    public void save(String data) {
        String currentUser = ThreadLocalIntro.staticCurrentUser.get(); // TODO
        // currentUser = SecurityContextHolder.getContext().getAuthentication().getName(); // in spring
        log.info("INSERT INTO A (data={}, updated_by={}) ", data, currentUser);
    }
}
