package victor.training.performance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UberEntityRepo extends JpaRepository<UberEntity, Long> {

    @Query("SELECT u FROM UberEntity u " +
           "LEFT JOIN FETCH u.invoicingCountry " +
           "LEFT JOIN FETCH u.fiscalCountry " + // !!! LEFT JOIN FETCH pe @ManyToOne
           "LEFT JOIN FETCH u.originCountry " +
           "LEFT JOIN FETCH u.createdBy " +
           "LEFT JOIN FETCH u.nationality " +
           "LEFT JOIN FETCH u.scope " )
    List<UberEntity> toate();
}
