package victor.training.performance.cache;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.managedTeacherIds WHERE u.username = ?1")
    Optional<User> getForLogin(String username);

}
