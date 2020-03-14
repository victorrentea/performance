package victor.training.performance.leaks;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.ConcurrencyUtil;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("load")
@RequiredArgsConstructor
public class SheepController {
    private final SheepService service;

    @GetMapping("create")
    public void create(@RequestParam(defaultValue = "Bisisica") String name) {
        log.debug("create " + name);
        service.create(name);
    }

    @GetMapping("search")
    public List<Sheep> search(@RequestParam(defaultValue = "Bisisica") String name) {
        log.debug("two");
        return service.search(name);
    }
}

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class SheepService {
    private final SheepRepo repo;

    public void create(String name) {
        log.debug("Calling external WS");
        ConcurrencyUtil.sleep2(500);
        log.debug("Persist");
        repo.save(new Sheep(name));
    }

    public List<Sheep> search(String name) {
        return repo.getByNameLike(name);
    }
}

interface SheepRepo extends JpaRepository<Sheep, Long> {
    List<Sheep> getByNameLike(String name);
}


@Entity
@Data
        // Don't
class Sheep {
    @GeneratedValue
    @Id
    private Long id;

    private String name;

    public Sheep(String name) {
        this.name = name;
    }

    public Sheep() {
    }
}