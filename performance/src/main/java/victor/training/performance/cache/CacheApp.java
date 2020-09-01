package victor.training.performance.cache;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@EnableCaching
@SpringBootApplication
public class CacheApp {
    public static void main(String[] args) {
        SpringApplication.run(CacheApp.class);
    }
}


@RestController
@RequiredArgsConstructor
class UserController {
    private final UserService service;

    @GetMapping("user/create") // 1/zi
    public void create () {
        service.create();
    }
    @GetMapping("user/count") // 100 / sec
    public long count () {
      return service.count();
    }
}

@Service
@RequiredArgsConstructor
class UserService {
    private final UserRepo userRepo;
@CacheEvict("user-count")
    public void create() {
        userRepo.save(new User());
    }
    @Cacheable(cacheNames = "user-count"/*, key = "#{key}"*/)
    public long count() {
       return userRepo.count();
    }

//    Map<cacheNameString, Map<List<Args>, returnValue>>
}
interface UserRepo extends JpaRepository<User, Long> {
}

@Data
@Entity
class User {
    @Id
    @GeneratedValue
    private Long id;
}