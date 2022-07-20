package victor.training.performance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UberEntityRepo extends JpaRepository<UberEntity, Long> {
    @Query("SELECT u FROM UberEntity u")
    List<UberEntity> all();

}
