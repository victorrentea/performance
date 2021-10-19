package victor.training.performance.cache;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

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
