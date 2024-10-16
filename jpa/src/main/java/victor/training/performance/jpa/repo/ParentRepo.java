package victor.training.performance.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import victor.training.performance.jpa.entity.Parent;
import victor.training.performance.jpa.entity.ParentSubselect;
import victor.training.performance.jpa.entity.ParentView;

import java.util.List;
import java.util.stream.Stream;

public interface ParentRepo extends JpaRepository<Parent, Long> {

  @Query("""
      SELECT p FROM Parent p
      LEFT JOIN FETCH p.children
      LEFT JOIN FETCH p.country
      """)
  List<Parent> findAllWithChildren();

  interface ParentProjection { // Spring generates an implementation of this interface
    Long getId();
    String getName();
    String getChildrenNames();
  }
  @Query(nativeQuery = true, value = """
      select p.id, 
             p.name,
             nvl(string_agg(c.name, ',') within group (order by c.name asc), '') 
                    as childrenNames
      from parent p
      left join child c on p.id = c.parent_id
      group by p.id, p.name
      """)
  List<ParentProjection> nativeQuery();


  @Query("""
    SELECT ps FROM ParentSubselect ps
    JOIN Parent p ON p.id = ps.id
    WHERE p.age > 10
    """) // can filter on main @Entity model
  List<ParentSubselect> subselect();

  @Query("""
    SELECT pv FROM ParentView pv
    JOIN Parent p ON p.id=pv.id
    WHERE p.age > 10
    """)
  List<ParentView> view();

  @Query("SELECT p FROM Parent p")
  Stream<Parent> streamAll();
}
