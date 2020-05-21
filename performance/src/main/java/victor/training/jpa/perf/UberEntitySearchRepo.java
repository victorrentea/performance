package victor.training.jpa.perf;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UberEntitySearchRepo {
    private final EntityManager em;

    public List<?> search(String name, Long invoiceCountry) {

        String jpql = "SELECT u.id,u.name " +
                "FROM UberEntity u WHERE 1=1 ";

        Query query = em.createQuery(jpql);

        return query.getResultList();
    }
}
