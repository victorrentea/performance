package victor.training.performance.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ParentRepo extends JpaRepository<Parent, Long> {

  // *** Paginated Search #1) fetch parents and lazy-load children
  @Query("SELECT p FROM Parent p WHERE p.name LIKE ?1")
  Page<Parent> findByNameLike(String namePart, Pageable page);


  // *** Paginated Search #2) fetch parentIds and fetch their data in stage2
  // driving query:
  @Query("SELECT p.id FROM Parent p WHERE p.name LIKE ?1")
  Page<Long> findIdsByNameLike(String namePart, Pageable page);

  // fetching query:
  @Query("SELECT p FROM Parent p " +
         "LEFT JOIN FETCH p.children " +
         "LEFT JOIN FETCH p.country " +
         "WHERE p.id IN ?1")
  Set<Parent> findParentsWithChildren(List<Long> parentIds);

  // distinct e scump
  @Query("SELECT p FROM Parent p" /*+
      " LEFT JOIN FETCH p.country c" +
      " LEFT JOIN FETCH c.region" +
      " LEFT JOIN FETCH p.children"*/
  )
  Page<Parent> finduMeu(PageRequest pageRequest);

  @Query("SELECT p.id FROM Parent p WHERE UPPER(p.name) LIKE UPPER('%' || ?1 || '%') ")
  Page<Long> findPageOfIds(String namePart, PageRequest pageRequest);

  @Query("SELECT p FROM Parent p" +
      " LEFT JOIN FETCH p.country c" +
      " LEFT JOIN FETCH c.region" +
      " LEFT JOIN FETCH p.children" +
      " WHERE p.id IN (?1)")
  Set<Parent> loadDetailsForSearchResults(List<Long> ids);
}
