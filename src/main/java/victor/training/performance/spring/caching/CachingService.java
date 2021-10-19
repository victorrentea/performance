package victor.training.performance.spring.caching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CachingService implements CommandLineRunner {
    private final UserRepo userRepo;
    private final CountryRepo countryRepo;

    @Override
    public void run(String... args) throws Exception {
        log.info("Persisting country static data");
        Stream.of("Romania", "Serbia", "Belgium").map(Country::new).forEach(countryRepo::save);
    }

    // TODO cache me
    public List<Country> getAllCountries() {
        return countryRepo.findAll();
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
