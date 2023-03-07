package victor.training.performance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParentSearchViewRepo extends JpaRepository<ParentSearchView, Long> {
  interface ParentSearchProjection { // 💡 sent as JSON
    Long getId();
    String getName();
    String getChildrenNames();
  }
  @Query(nativeQuery = true,value =
          "select p.ID as id," +
          " P.NAME as name, " +
          " nvl(STRING_AGG(c.NAME, ',') within group (order by c.name asc), '') as childrenNames " +
          " from PARENT P" +
          " left join CHILD C on P.ID = C.PARENT_ID" + // TODO eat a " "
          " group by p.ID, P.NAME")
  List<ParentSearchProjection> nativeQueryEquivalentOfView();


  @Query("SELECT psv FROM ParentSearchView psv " +
         "JOIN Parent p ON p.id = psv.id " +
         "WHERE p.age > 40")
    // we can traverse and filter on any property of our main JPA model
  ParentSearchView selectFromView_butFilterOnEntityModel();}
