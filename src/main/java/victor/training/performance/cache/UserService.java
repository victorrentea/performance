package victor.training.performance.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;


    public long countUsers() {
        return userRepo.count();
    }

    // TODO 1 Cacheable
    // TODO 2 EvictCache
    // TODO 3 Prove: Cache inconsistencies on multiple instances: start a 2nd instance usign -Dserver.port=8081
    // TODO 4 Redis cache
    public void createUser() {
        userRepo.save(new User("John-" + System.currentTimeMillis()));
    }

    // TODO 5 key-based cache entries
    @Cacheable("user-data")
    public UserDto getUser(long id) {
        return new UserDto(userRepo.findById(id).get());
    }

    public void updateUser(long id, String newName) {
        // TODO 6 update profile too -> pass Dto
        User user = userRepo.findById(id).get();
        user.setName(newName);
    }
}

