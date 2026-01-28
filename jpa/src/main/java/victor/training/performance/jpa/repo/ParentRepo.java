package victor.training.performance.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import victor.training.performance.jpa.entity.Parent;
import victor.training.performance.jpa.entity.ParentSubselect;
import victor.training.performance.jpa.entity.ParentView;

import java.util.List;
import java.util.stream.Stream;

public interface ParentRepo extends JpaRepository<Parent, Long> {


  // max 1 "AND"
  Parent findByNameLikeIgnoreCaseAndAgeBetween(
      String namePart, int ageFrom, int ageTo);



  // Spring generates an implementation of this interface at runtime
//  interface ParentProjection { // cam multa magie🤢
//    Long getId();
//    String getName(); // property 'name' must match the column name
//    String getChildrenNames();
//  }

  // sau mai traditie,
  record ParentProjection(Long id, String name, String childrenNames) {
  } // sau @lobmok.Value class ..

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
  // string_agg support (lock-in?):
  //  ✅ PostgreSQL
  //  ✅ DB2
  //  ✅ SQL Server (2017+)
  //  ❌ MySQL -> use GROUP_CONCAT
  //  ❌ Oracle -> use LISTAGG
  //  ❌ SQLite
  List<ParentProjection> nativeQuery();


  @Query("""
    SELECT ps FROM ParentSubselect ps
    JOIN Parent p ON p.id = ps.id
    WHERE p.age > 10
    """) // back in JPQL, so I can use my 💖 @Entity model in WHERE
  List<ParentSubselect> subselect();

  @Query("""
    SELECT pv FROM ParentView pv
    JOIN Parent p ON p.id=pv.id
    WHERE p.age > 10
    
    """) // JPQL + the VIEW is compiled by DB => early error detection
  List<ParentView> view();

  @Query("SELECT p FROM Parent p")
  Stream<Parent> streamAll();
}
