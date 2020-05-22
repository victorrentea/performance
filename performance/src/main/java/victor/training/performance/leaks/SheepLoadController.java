package victor.training.performance.leaks;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
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

@Slf4j
@RestController
@RequestMapping("load")
@RequiredArgsConstructor
public class SheepLoadController {
    private final SheepService service;

    @GetMapping("create")
    public Long create(@RequestParam(defaultValue = "Bisisica") String name) {
        log.debug("create " + name);
        return service.create(name);
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
    public Long create(String name) {
        String sn = shepard.registerSheep(name);
//        TransactionTemplate // e alta solutie programatica
        return altaMetCuTxInAccesiClasa(name, sn);
    }

    @Transactional // nici un proxy nu vede aceasta adnotare - efectiv nu functioneaza
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