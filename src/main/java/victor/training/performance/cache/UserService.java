package victor.training.performance.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

class Country {}
interface CountryRepo {}
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;

//    @Cacheable("country")
//    public List<Country> getCountryList() {
//        return countryRepo.findAll();
//    }

    // the only way to insert a user in the app is via an INSERT (OMG)??!

    @Cacheable("count-users")
    public long countUsers() {
        return userRepo.count();
    }

    @Cacheable("all-user-data")
    public List<User> getAll() {
        return userRepo.findAll();
    }


    // TODO 1 Cacheable
    // TODO 2 EvictCache
    // TODO 3 Prove: Cache inconsistencies on multiple instances: start a 2nd instance usign -Dserver.port=8081
    // TODO 4 Redis cache
    @CacheEvict(cacheNames = {"user-data", "count-users"})
    public void createUser() {
        userRepo.save(new User("John-" + System.currentTimeMillis()));
    }

    // =================================


    // TODO 5 key-based cache entries
    @Cacheable("user-data")
    public UserDto getUser(long id) {
        return new UserDto(userRepo.findById(id).get());
    }

    @CacheEvict(value = "user-data", key = "#id")
    public void updateUser(long id, String newName) {
        // TODO 6 update profile too -> pass Dto
        User user = userRepo.findById(id).get();
        user.setName(newName);
    }


}

