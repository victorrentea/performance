package victor.training.performance.jpa.parent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParentSearchViewRepo extends JpaRepository<ParentSearchViewEntity, Long> {
//  @Data
//  class ParentSearchDto { // 💡 sent as JSON
//    private Long id;
//    private String name;
//    private String childrenNames;
//  }
//  @Query(value = """
//    select p.ID,
//       P.NAME,
//       nvl(STRING_AGG(c.NAME, ',') within group (order by c.name asc), '') children_names
//    from PARENT P
//             left join CHILD C on P.ID = C.PARENT_ID
//    group by p.ID, P.NAME
//    """, nativeQuery = true)
//  List<ParentSearchDto> nativeQueryForDto();

  // Spring Projection
  interface ParentSearchProjection { // 💡 sent as JSON
    Long getId();
    String getName();
    String getChildrenNames();
  }
  @Query(nativeQuery = true, value = """
              select 
                P.ID as id, 
                P.NAME as name,
                nvl(STRING_AGG(c.NAME, ',') within group (order by c.name asc), '') as childrenNames
              from PARENT P
              left join CHILD C on P.ID = C.PARENT_ID
              group by p.ID, P.NAME
              """)
  List<ParentSearchProjection> nativeQueryForProjections();


  @Query("""
      SELECT psv FROM ParentSearchViewEntity psv
      JOIN Parent p ON p.id = psv.id
      WHERE p.age > 40
      """)
    // we can traverse and filter on any property of our main JPA model
  ParentSearchViewEntity selectFromView_butFilterOnEntityModel();
}
