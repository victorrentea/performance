package victor.training.performance.jpa.uber;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.List;

public interface UberEntityRepo extends JpaRepository<UberEntity, Long> {
  interface UberStatusProjection {
    UberEntity.Status getStatus();
  }
//  @Data
//  class UberStatusDto {
//    private UberEntity.Status status;
//  }
  @Query("SELECT u.status as status FROM UberEntity u WHERE u.id=?1")
  UberStatusProjection getStatusById(long id);

  @Query("SELECT u FROM UberEntity u")
  List<UberEntity> findAllWithQuery();

  @Query("SELECT u FROM UberEntity u " +
         "WHERE (:name is null OR UPPER(u.name) LIKE UPPER('%' || :name || '%'))")
  List<UberEntity> searchFixedJqpl(@Nullable String name);


  List<UberEntity> findByName(String name); // results in generated JPQL
}
