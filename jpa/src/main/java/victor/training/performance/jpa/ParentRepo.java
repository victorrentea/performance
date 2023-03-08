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
             " LEFT JOIN FETCH p.children"
//             + " JOIN FETCH p.country"
      )
      List<Parent> findAllFetchChildren();



    @Query("SELECT p FROM Parent p") // 😔 Spring Projections fetch all the fields
//    @Query("SELECT p.id as id, p.name as name FROM Parent p") // 😔 explicit listing fields => does not support hierarchical
    Set<ParentProjected> findAllProjected();


//  @Query("SELECT p FROM Parent p LEFT JOIN FETCH p.children WHERE p.name LIKE ?1") // ILLEGAL to paginate and fetch children
  // startup fails
  @Query("SELECT p FROM Parent p WHERE p.name LIKE ?1")
  Page<Parent> findByNameLike(String namePart, Pageable page);


}
