package victor.training.performance.cache;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping("users")
    public List<UserDto> getAll() {
        List<User> all = service.getAll();
        return all.stream().map(UserDto::new).collect(Collectors.toList());
    }
    @GetMapping("users/count")
    public long count() {
        return service.countUsers();
    }
    @GetMapping("users/create")
    public void create() {
        service.createUser();
    }

    @GetMapping("users/{id}")
    public UserDto get(@PathVariable long id) {
        return service.getUser(id);
    }
    // correct REST way
//    @PutMapping("users/{id}")
//    public void update(@PathVariable long id, @RequestBody UserDto dto) {
//        service.updateUser(id, dto.name);
//    }


    //    @Scheduled(cron = "* 0 * * 0 *")
//    @Scheduled(fixedDelay = 1000 * 60 * 60)
    @GetMapping("users/kill-cache")
    // 1) click me after messing with DB directly
    // 2) at the end of an import sript (BASH) curl localhost:8080/users/kill-cache
    @CacheEvict(cacheNames = "all-user-data")
    public void killUserCache() {
    }


    // hacked for convenience (called from browser)
    @GetMapping("users/{id}/update")
    public void update(@PathVariable long id) {
        String newName = RandomStringUtils.randomAlphabetic(10);
        UserDto dto = new UserDto();
        dto.id = id;
        dto.name = newName;
        dto.profile = UserRole.ADMIN;
        service.updateUser(id, dto.name);
    }

}
