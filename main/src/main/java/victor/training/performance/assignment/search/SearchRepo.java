package victor.training.performance.assignment.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SearchRepo {
    private final EntityManager em;

    public List<Car> search(CarSearchCriteria criteria) {
        Map<String, Object> params = new HashMap<>();
        String jpql = "SELECT c FROM Car c WHERE 1=1 ";

        if (criteria.make != null) {
            jpql += " AND UPPER(c.make) LIKE ('%' || UPPER(:make) || '%')";
            params.put("make", criteria.make);
        }
        if (criteria.year != null) {
            jpql += " AND c.year = :year";
            params.put("year", criteria.year);
        }
        TypedQuery<Car> query = em.createQuery(jpql, Car.class);
        for (String key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }
        // simulate page of 50 results
        int pageSize = criteria.pageSize != null ? criteria.pageSize : 50;
        int pageNumber = criteria.pageNumber != null ? criteria.pageNumber : 0;
        query.setFirstResult(pageSize * pageNumber);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
}
