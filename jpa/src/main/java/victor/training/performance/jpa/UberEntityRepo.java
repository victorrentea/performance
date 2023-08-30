package victor.training.performance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.List;

public interface UberEntityRepo extends JpaRepository<UberEntity, Long> {
  @Query("SELECT u FROM UberEntity u")
  List<UberEntity> findAllWithQuery();

  @Query("SELECT u FROM UberEntity u " +
         "WHERE (:name is null OR UPPER(u.name) LIKE UPPER('%' || :name || '%'))")
  List<UberEntity> searchFixedJqpl(@Nullable String name);

  boolean existsByIdAndStatus(Long id, UberEntity.Status status);

  List<UberEntity> findByName(String name);
}
