package victor.training.performance.leaks;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
public class SheepController {
    private final SheepService service;

    @GetMapping("create")
    public CompletableFuture<Long> create(@RequestParam(defaultValue = "Bisisica") String name) {
        log.debug("create {}", name);
        return service.create(name);


    }
    // TODO Starve Connections
    // TODO Starve Threads

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

//    @Transactional
    public CompletableFuture<Long> create(String name) {
        return shepard.registerSheep(name)
            .thenApply(sn -> repo.save(new Sheep(name, sn)).getId());
    }
//@Transactional
    public List<Sheep> search(String name) {
        return repo.getByNameLike(name);
    }
}
@Slf4j
@Service
class ShepardService {
    @Async
    public CompletableFuture<String> registerSheep(String name) {
        log.debug("Calling external WS");
        ConcurrencyUtil.sleepq(500);
        return completedFuture(UUID.randomUUID().toString());
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
    @Setter(AccessLevel.NONE)
    private Long id;

    private String name;
    private String sn;

    public Sheep() {}
    public Sheep(String name, String sn) {
        this.name = name;
        this.sn = sn;
    }
}