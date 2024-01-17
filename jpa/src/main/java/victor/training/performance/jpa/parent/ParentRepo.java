package victor.training.performance.jpa.parent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ParentRepo extends JpaRepository<Parent, Long> {

  @Query("""
      SELECT p
      FROM Parent p
        LEFT JOIN FETCH p.children
        LEFT JOIN FETCH p.country""")
  List<Parent> fetchParentFull();

  //Spring Projections: interfata fara implementare -> Spring genereaza implementarea
  interface ParentProjection { // Spring generates an implementation of this interface
    Long getId();
    String getName();
    String getChildrenNames();
  }
  @Query("""
      select p.id,
             p.name,
             'pisici' as childrenNames
      from Parent p
      """)
  List<ParentProjection> projectjqpl();


  @Query(nativeQuery = true, value = """
      select p.id, 
             p.name,
             nvl(string_agg(c.name, ',') within group (order by c.name asc), '') as childrenNames
      from parent p
      left join child c on p.id = c.parent_id
      
      group by p.id, p.name
      """)
  List<ParentProjection> nativeQuery();
//  List<Object[]> nativeQuery(); // scarbos; nu stii ce coloane ai [0] Long?BigDecimal? ce coloan e de fapt


  // ramai in JPQL, ingropi SQL nativ doar in @Subselect
  @Query("""
    SELECT ps FROM ParentSubselect ps
    JOIN Parent p ON p.id = ps.id
    WHERE p.age > 10
    """) // can filter on main @Entity model ❤️
  Page<ParentSubselect> subselect(PageRequest pageRequest);

  @Query("""
    SELECT pv FROM ParentView pv
    """)
  List<ParentView> view();


  // A) Retrieve hierarchical data allowing lazy load to trigger
  @Query("SELECT p FROM Parent p WHERE p.name LIKE ?1")
  Page<Parent> searchByNameLike(String namePart, Pageable pageable);

  // B) Retrieve hierarchical data via Driving Query
  @Query("SELECT p.id FROM Parent p WHERE p.name LIKE ?1")
  Page<Long> findIdsPage(String namePart, Pageable pageable); // #1 driving
  @Query("SELECT p.id FROM Parent p WHERE p.name LIKE ?1")
  List<Long> findIds(String namePart); // #1 driving
  @Query("""
      SELECT p
      FROM Parent p
        LEFT JOIN FETCH p.children
        LEFT JOIN FETCH p.country
      WHERE p.id IN ?1""")
  Set<Parent> fetchParentsByIds(List<Long> parentIds); // #2 fetching
}
