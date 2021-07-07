package victor.training.performance.leaks;

import io.micrometer.core.annotation.Timed;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
    public Long createSheep(@RequestParam(defaultValue = "Bisisica") String name) {
        log.debug("create " + name);
        return service.create(name);
    }
    // TODO Starve Connections
    // TODO Starve Threads

    @GetMapping("search")
    public List<Sheep> searchSheep(@RequestParam(defaultValue = "Bisisica") String name) {
        log.debug("search for " + name);
        return service.search(name);
    }
}

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class SheepService {
    private final SheepRepo repo;
    private final ShepardService shepard;

    public Long create(String name) {
        String sn = shepard.registerSheep(name);
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
    @Timed("shepard")
    public String registerSheep(String name) {
        SheepRegistrationResponse response = new RestTemplate()
            .getForObject("http://localhost:9999/api/register-sheep", SheepRegistrationResponse.class);
        return response.getSn();
//        log.debug("Calling external WS");
//        PerformanceUtil.sleepq(500);
//        return UUID.randomUUID().toString();
    }
}
@Data
class SheepRegistrationResponse {
    private String sn;
}

interface SheepRepo extends JpaRepository<Sheep, Long> {
    List<Sheep> getByNameLike(String name);
}


@Entity
@Data // just a demo
class Sheep {
    @GeneratedValue
    @Id
    private Long id;

    private String name;
    private String sn;

    public Sheep() {}
    public Sheep(String name, String sn) {
        this.name = name;
        this.sn = sn;
    }
}