package victor.training.performance.spring.caching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
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
        if (siteRepo.count() == 0) { // canb you spot the race bug here? :)
            Stream.of("Romania", "Serbia", "Belgium").map(Site::new).forEach(siteRepo::save);
        }
    }

    // TODO cache me
    @Cacheable("sites")
    public List<Site> getAllSites() {
        System.out.println(" a doua oara, n-o vezi");
        return siteRepo.findAll();
    }
    // TODO imagine direct DB access (manual or script)

    // =========== editable data ===========


    // TODO cache me
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // TODO Evict
    public String createUser() {
        Long id = userRepo.save(new User("John-" + System.currentTimeMillis())).getId();
        return "Created id: " + id;
    }


    // TODO key-based cache entries
    public UserDto getUser(long id) {
        return new UserDto(userRepo.findById(id).get());
    }

    // TODO Evict
    public void updateUser(long id, String newName) {
        // TODO 6 update profile too -> pass Dto
        User user = userRepo.findById(id).get();
        user.setUsername(newName);
    }


}

