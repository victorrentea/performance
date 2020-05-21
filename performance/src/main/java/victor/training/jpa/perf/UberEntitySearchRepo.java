package victor.training.jpa.perf;

import lombok.RequiredArgsConstructor;
import lombok.Value;
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

        String jpql = "SELECT new victor.training.jpa.perf.UberSearchResult(u.id,u.name,u.invoicingCountry.name) " +
                "FROM UberEntity u   WHERE 1=1 ";

        // DACA transformi query-ul intr-un view, poti in continuare sa
        // ramai in JPQL si sa faci LEFT JOIN Parent p ON p.name = u.name ca
        // sa vii in restul modelul JPA

        TypedQuery<UberSearchResult> query = em.createQuery(jpql, UberSearchResult.class);

        return query.getResultList();
    }
}


@Value
class UberSearchResult {
    long id;
    String name;
    String invoiceCountryName;
}