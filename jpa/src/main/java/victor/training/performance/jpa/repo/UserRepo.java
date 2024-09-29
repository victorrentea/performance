package victor.training.performance.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import victor.training.performance.jpa.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {

}
