package victor.training.performance.jpa.parent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ParentRepo extends JpaRepository<Parent, Long> {

  // ***A) Paginated Search #1) fetch parents and lazy-load children
  @Query("SELECT p FROM Parent p WHERE p.name LIKE ?1")
  Page<Parent> searchByNameLike(String namePart, Pageable pageable);


  // ***B) Paginated Search #2) fetch parentIds and fetch their data in stage2
  // 1: driving query
  @Query("SELECT p.id FROM Parent p WHERE p.name LIKE ?1")
  Page<Long> searchIdsByNameLike(String namePart, Pageable pageable);

  // 2: fetching query
  @Query("""
      SELECT p
      FROM Parent p
        LEFT JOIN FETCH p.children
        LEFT JOIN FETCH p.country
      WHERE p.id IN ?1""")
  Set<Parent> fetchParentsWithChildren(List<Long> parentIds);

  // *** @Subselect
  @Query("SELECT pss FROM ParentSearchSubselect pss WHERE pss.name LIKE ?1")
  Page<ParentSearchSubselect> searchSubselect(String namePart, Pageable pageable);

  // ii spun explicit lui Hib sa-mi preincarce copiii cu un JOIN
  // DISTINCT ii spune lui hib sa elimine din List<> intoarsa dublurile, pe langa DISTINCTul din SQL
  @Query("""
    SELECT p
    FROM Parent p
    LEFT JOIN FETCH p.children
    LEFT JOIN FETCH p.country
    """)
  // Inner JOIN = lasa doar parintii cu copii
  // LEFT JOIN = aduce si parintii fara copii
  Set<Parent> fetchAllWithChildren();
}
