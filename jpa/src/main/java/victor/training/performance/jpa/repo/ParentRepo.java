package victor.training.performance.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import victor.training.performance.jpa.entity.Parent;
import victor.training.performance.jpa.entity.ParentSubselect;
import victor.training.performance.jpa.entity.ParentView;

import java.util.List;
import java.util.stream.Stream;

public interface ParentRepo extends JpaRepository<Parent, Long> {

  // Spring generates an implementation of this interface
  interface ParentProjection {
    Long getId();
    String getName(); // property 'name' must match the column name
    String getChildrenNames();
  }
  @Query(nativeQuery = true, value = """
      select p.id,
             p.name,
             nvl(string_agg(c.name, ',') 
               within group (order by c.name asc), '') 
                 as childrenNames
      from parent p
      left join child c on p.id = c.parent_id
      group by p.id, p.name
      """)
  // PostgreSQL ✅, SQL Server (2017+) ✅, MySQL ❌ (use GROUP_CONCAT),
  // Oracle ❌ (use LISTAGG), SQLite ❌, DB2 ✅
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
    """) // VIEW syntax is compiled by DB
  List<ParentView> view();

  @Query("SELECT p FROM Parent p")
  Stream<Parent> streamAll();
}
