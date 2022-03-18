package victor.training.performance.spring.caching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.User;
import victor.training.performance.jpa.UserRepo;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CachingService implements CommandLineRunner {
    private final UserRepo userRepo;
    private final SiteRepo siteRepo;

    @Override
    public void run(String... args) throws Exception {
        log.info("Persisting site static data");
        Stream.of("Romania", "Serbia", "Belgium").map(Site::new).forEach(siteRepo::save);
    }

    // TODO cache me
    @Cacheable("countries")
    public List<Site> getAllCountries() {
        return siteRepo.findAll();
    }
    // TODO imagine direct DB access (manual or script)

    // =========== editable data ===========

    @Cacheable("all-users")
    // tre sa-ti inchipui o Map<Void, List<User>> users;
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @CacheEvict("all-users")
    public String createUser() {
        Long id = userRepo.save(new User("John-" + System.currentTimeMillis())).getId();
        return "Created id: " + id;
    }


    @Cacheable("user")
    public UserDto getUser(long id) {
        return new UserDto(userRepo.findById(id).get());
    }

    @CacheEvict(value = "user",key = "#id")
//    @CacheEvict(value = "all-users", allEntries = true)
    public void updateUser(long id, String newName) {
//        Object user = cacheManager.getCache("user").get(id).get();
//        if ()
        // TODO 6 update profile too -> pass Dto
        User user = userRepo.findById(id).get();
        user.setUsername(newName);
    }

    private final CacheManager cacheManager; // daca nu-ti place @Cacheable ca e prea multa magie


}

