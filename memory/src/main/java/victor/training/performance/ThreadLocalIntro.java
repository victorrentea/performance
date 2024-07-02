package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.symmetric.AES.CFB;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ThreadLocalIntro {
    private final AController controller = new AController(new AService(new ARepo()));
    public static void main(String[] args) throws InterruptedException, ExecutionException {
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
//    public static ThreadLocal<String> staticCurrentUser = new InheritableThreadLocal<>(); // NU FUNCTIONEAZA DECAT DACA TU CREEZI THREADURI (bad practice cu Thread pool)
    // !! ar merge in java 21 VirtualThreads -> mai bine foloseste Scoped Variable (Java 25 LST)
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
        log.info("Start " + ThreadLocalIntro.staticCurrentUser.get());
        service.create(data);
        // Cerinta: toate logurile unui batch sa aiba un TOKEN comun in mesajul de log ca sa pot identifica tot ce a facut batch
    }
}
// ----------- Service ------------
@Service
@RequiredArgsConstructor
class AService {
    private final ARepo repo;
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();

    {
        pool.setCorePoolSize(1);
        pool.setMaxPoolSize(1);
        pool.setQueueCapacity(100);
        pool.setTaskDecorator(r -> {
            // propaga MDC context din thread parinte peste catre worker thread
            // rulez in parent thread
            Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
            String useruDinThreadulParinte = ThreadLocalIntro.staticCurrentUser.get();
            return () -> {
                // rulez in worker thread
                MDC.setContextMap(copyOfContextMap);
                ThreadLocalIntro.staticCurrentUser.set(useruDinThreadulParinte);
                // mai poti copia aici SecurityContextHolder, TraceID, Opentelemetry Context
                r.run(); // face munca efectiva
            };
        });
        pool.initialize();
    }

    public void create(String data) {
        sleepMillis(10); // some delay, to reproduce the race bug

        pool.submit(()->repo.save(data)); // toate metodele din app mea vor sfarsi avand username param = GU-NOI!
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
