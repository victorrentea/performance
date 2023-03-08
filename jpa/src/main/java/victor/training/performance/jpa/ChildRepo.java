package victor.training.performance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Stream;

public interface ChildRepo extends JpaRepository<Child, Long> {
  Stream<Child> findChildByParentId(Long parentId); // hibernate will open a SQL SELECT to DB and give you stream that when you iterate through,
  // the underlying ResultSet would be traversed
}
