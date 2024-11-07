package victor.training.performance.jpa.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import victor.training.performance.jpa.entity.Parent;

import java.util.List;
import java.util.Set;

public interface ParentSearchRepo extends JpaRepository<Parent, Long> {

  // A) Retrieve hierarchical data allowing lazy load to trigger
  @Query("""
      SELECT p 
      FROM Parent p 
      LEFT JOIN FETCH p.children
      LEFT JOIN FETCH p.country
      WHERE p.name LIKE ?1
      """)
  Page<Parent> searchByNameLike(String namePart, Pageable pageable);

  // B) Retrieve hierarchical data via Driving Query
  // 1: Find IDs
  @Query("SELECT p.id FROM Parent p WHERE p.name LIKE ?1")
  Page<Long> findIdsPage(String namePart, Pageable pageable); // #1 driving

  // 2: Fetch by IDs
  @Query("""
      SELECT p
      FROM Parent p
        LEFT JOIN FETCH p.children
        LEFT JOIN FETCH p.country
      WHERE p.id IN ?1""")
  Set<Parent> fetchParentsByIds(List<Long> parentIds); // #2 fetching
}
