package victor.training.performance.leaks;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import victor.training.performance.ConcurrencyUtil;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

@Slf4j
@RestController
@RequestMapping("load")
@RequiredArgsConstructor
public class SheepLoadController {
    private final SheepService service;

    @GetMapping("create")
    public DeferredResult<ResponseEntity<Long>> create(@RequestParam(defaultValue = "Bisisica") String name) {
        log.debug("create " + name);
        DeferredResult<ResponseEntity<Long>> deferred = new DeferredResult<>();
        service.create(name)
                .thenAccept(id -> deferred.setResult(ResponseEntity.ok(id)))
                .exceptionally(e -> {
                    deferred.setResult(ResponseEntity.status(500).build());
                    return null;
                });
        log.debug("eliberez threadul din HTTP connection pool pe care s-a rulat /load/create");
        return deferred; // dummy
    }
    // Hit using JMeter and:
    // TODO Starve Connections
    // TODO Starve Threads
    // ---> Fix by async

    @GetMapping("search")
    public List<Sheep> search(@RequestParam(defaultValue = "Bisisica") String name) {
        log.debug("search for " + name);
        return service.search(name);
    }
}

@Slf4j
@Service
@RequiredArgsConstructor
class SheepService {
    private final SheepRepo repo;
    private final ShepardService shepard;

//    @Transactional // evita sa pui @Transactional pe metode care dureaza mult timp din cauze externe (no-DB)
    // eg: WS call, parsari de fisiere mari.
    // pentru ca tii DB connectionul blocat, in timp ce altii asteapta
    @SneakyThrows
    @Async("sheepCreateExecutor")
    public CompletableFuture<Long> create(String name) {
        String sn = shepard.registerSheep(name);
//        String sn = shepard.registerSheep(name).get(); // faci .get imediat ca sa limitzi concurenta outbound
//        TransactionTemplate // e alta solutie programatica

        return completedFuture(altaMetCuTxInAccesiClasa(name, sn));
    }

    //@Transactional // nici un proxy nu vede aceasta adnotare - efectiv nu functioneaza
    public Long altaMetCuTxInAccesiClasa(String name, String sn) {
        log.debug("Persist");
        Sheep sheep = repo.save(new Sheep(name, sn));
        return sheep.getId();
    }

    public List<Sheep> search(String name) {
        return repo.getByNameLike(name);
    }
}
@Slf4j
@Service
class ShepardService {
//    @Async("shepardProtectionExecutor")
//    public CompletableFuture<String> registerSheep(String name) {
    public String registerSheep(String name) {
        log.debug("Calling external WS");
        ConcurrencyUtil.sleep2(500);
        return UUID.randomUUID().toString();
    }
}

interface SheepRepo extends JpaRepository<Sheep, Long> {
    List<Sheep> getByNameLike(String name);
}


@Entity
@Data // Don't
class Sheep {
    @GeneratedValue
    @Id
    private Long id;

    private String name;
    private String sn;

    protected Sheep() {}
    public Sheep(String name, String sn) {
        this.name = name;
        this.sn = sn;
    }
}