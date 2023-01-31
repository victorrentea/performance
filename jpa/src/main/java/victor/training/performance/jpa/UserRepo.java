package victor.training.performance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u  WHERE u.username = ?1")
    Optional<User> getForLogin(String username);

}
