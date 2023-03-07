package victor.training.performance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParentSearchViewRepo extends JpaRepository<ParentSearchView, Long> {
  @Query("SELECT psv FROM ParentSearchView psv JOIN Parent p ON p.id = psv.id WHERE p.age > 40")
  ParentSearchView selectFromAggregatedView_butQueryOnEntityModel();
}
