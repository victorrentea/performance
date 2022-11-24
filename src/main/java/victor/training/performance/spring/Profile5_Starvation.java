package victor.training.performance.spring;

import io.micrometer.core.annotation.Timed;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("profile/sheep")
@RequiredArgsConstructor
class SheepController {
    private final SheepService service;

    @GetMapping("create")
    public Long createSheep(@RequestParam(required = false) String name) {
        if (name == null) {
            name = "Bisisica " + LocalDateTime.now();
        }
        log.debug("create " + name);
        return service.create(name);
    }

    @GetMapping("search")
    public List<Sheep> searchSheep(@RequestParam(defaultValue = "Bisisica%") String name) {
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
        System.out.println("Magia proxy-urilor (daca n-o stii pasta, scoate Spring din CV): "
            + shepard.getClass().getName());
        String sn = shepard.registerSheep(name); // Takes 1 second (HTTP call)
        Sheep sheep = repo.save(new Sheep(name, sn));
        return sheep.getId();
    }
    public List<Sheep> search(String name) {
        return repo.getByNameLike(name);
    }
}
@Slf4j
@Service
@RequiredArgsConstructor
class ShepardService {
    private final ShepardClient client;


    // mai fain decat t0 t1= t1-t0
    @Timed("shepard") // ii spune lui spring sa introduca un proxy in fata metodei
    // care intercepteaza apelul masoara cat a durat, raportand apoi pe http://localhost:8080/actuator/prometheus
    // ca sa-l poti urmari pe grafana
    public String registerSheep(String name) {
        SheepRegistrationResponse response = new RestTemplate()
            .getForObject("http://localhost:9999/api/register-sheep", SheepRegistrationResponse.class);

        // or, using Feign client
        // SheepRegistrationResponse response = client.registerSheep();
        return response.getSn();
    }
}

@FeignClient(name = "shepard", url="http://localhost:9999/api")
interface ShepardClient {
    @GetMapping("register-sheep")
    SheepRegistrationResponse registerSheep();
}
@Data
class SheepRegistrationResponse {
    private String sn;
}

interface SheepRepo extends JpaRepository<Sheep, Long> {
    List<Sheep> getByNameLike(String name);
}


@Entity
@Data
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

//@Configuration //TODO uncomment me
class SomeConfig {
    @Bean
    public ThreadPoolTaskExecutor shepardPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("shepard-");
        executor.initialize();
        return executor;
    }

}