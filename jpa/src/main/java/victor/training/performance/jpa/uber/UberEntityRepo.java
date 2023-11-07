package victor.training.performance.jpa.uber;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Null;
import java.util.List;

import static victor.training.performance.jpa.uber.UberEntity.*;

public interface UberEntityRepo extends JpaRepository<UberEntity, Long> {
  @Query("SELECT u FROM UberEntity u")
  List<UberEntity> findAllWithQuery();

  @Query("SELECT u FROM UberEntity u " +
         "WHERE (:name is null OR UPPER(u.name) LIKE UPPER('%' || :name || '%')) " +
         "AND (:status is null OR u.status = :status)")
  List<UberEntity> searchFixedJqpl(@Nullable String name, @Nullable Status status);

  List<UberEntity> findByName(String name);
}
