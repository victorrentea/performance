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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("profile/sheep")
@RequiredArgsConstructor
class SheepController {
    private final SheepService service;

    @GetMapping("create")
    public CompletableFuture<Long> createSheep(@RequestParam(required = false) String name) {
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
class SheepService {
    private final SheepRepo repo;
    private final ShepardService shepard;
    public CompletableFuture<Long> create(String name) {
        return shepard.registerSheep(name)
            .thenApply(sn ->repo.save(new Sheep(name, sn)))
            .thenApply(sheep -> sheep.getId());
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
    @Timed("shepard")
//    @Async("shepardPool")
    public CompletableFuture<String> registerSheep(String name) {
        return new AsyncRestTemplate()
            .getForEntity("http://localhost:9999/api/register-sheep", SheepRegistrationResponse.class)
            .completable()
            .thenApply(response -> response.getBody().getSn());
        // or, using Feign client

//        SheepRegistrationResponse response = client.registerSheep(); // HTTP
//        return CompletableFuture.completedFuture(response.getSn());
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
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("shepard-");
        executor.initialize();
        return executor;
    }

}