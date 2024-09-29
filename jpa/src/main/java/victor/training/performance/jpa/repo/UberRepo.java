package victor.training.performance.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import victor.training.performance.jpa.entity.Uber;

import java.util.List;
import java.util.stream.Stream;

import static victor.training.performance.jpa.entity.Uber.Status;

public interface UberRepo extends JpaRepository<Uber, String> {
  @Query("SELECT u FROM Uber u")
  List<Uber> findAllWithQuery();

  @Query("SELECT u FROM Uber u " +
         "WHERE (:name is null OR UPPER(u.name) LIKE UPPER('%' || :name || '%')) " +
         "AND (:status is null OR u.status = :status)")
  List<Uber> searchFixedJqpl(@Nullable String name, @Nullable Status status);

  List<Uber> findByName(String name);

  @Query("FROM Uber")
  Stream<Uber> streamAll();
}
