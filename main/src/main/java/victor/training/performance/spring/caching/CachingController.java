package victor.training.performance.spring.caching;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.jpa.User.UserRole;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class CachingController {
    private final CachingService service;

    // ---- static data ----
    @GetMapping("sites")
    public List<SiteDto> getAllSites() {
        return service.getAllSites().stream().map(SiteDto::new).collect(toList());
    }

    // ---- editable data ----
    @GetMapping("users")
    public List<UserDto> getAllUsers() {
        return service.getAllUsers().stream().map(UserDto::new).collect(toList());
    }
    @GetMapping("users/count")
    public long countUsers() {
        return service.getAllUsers().size();
    }
    @GetMapping("users/create")
    public String createUser() {
        return service.createUser();
    }

    @GetMapping("users/{id}") // (mis)using GET for testing from browser
    public UserDto getUser(@PathVariable long id) {
        return service.getUser(id);
    }

    @GetMapping("users/{id}/update") // (mis)using GET for testing from browser
    public void updateUser(@PathVariable long id) {
        String newName = RandomStringUtils.randomAlphabetic(10);
        UserDto dto = new UserDto();
        dto.id = id;
        dto.username = newName;
        dto.profile = UserRole.ADMIN;
        service.updateUser(id, dto.username);
    }

}
