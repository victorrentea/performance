package victor.training.performance.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import victor.training.performance.jpa.projections.ParentProjected;

import java.util.List;
import java.util.Set;

public interface ParentRepo extends JpaRepository<Parent, Long> {


      @Query("SELECT DISTINCT p FROM Parent p" +
             " LEFT JOIN FETCH p.children")
      List<Parent> findAllFetchChildren();



    @Query("SELECT p FROM Parent p") // 😔 Spring Projections fetch all the fields
//    @Query("SELECT p.id as id, p.name as name FROM Parent p") // 😔 explicit listing fields => does not support hierarchical
    Set<ParentProjected> findAllProjected();


  @Query("SELECT p FROM Parent p WHERE p.name LIKE ?1")
  Page<Parent> findByNameLike(String namePart, Pageable page);


  //   @Query("SELECT p.id FROM Parent p WHERE p.name LIKE ?1")
  //   Page<Long> findByNameLike(String namePart, Pageable page);

  //   @Query("SELECT p FROM Parent p LEFT JOIN FETCH p.children WHERE p.id IN ?1")
  //   Set<Parent> findParentsWithChildren(List<Long> parentIds)

}
