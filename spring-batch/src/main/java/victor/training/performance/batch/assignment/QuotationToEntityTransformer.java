package victor.training.performance.batch.assignment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
public class QuotationToEntityTransformer implements ItemProcessor<QuotationRecord, Quotation> {
    @Autowired
    private EntityManager em;

    @Override
    public Quotation process(QuotationRecord record) throws Exception {
        Quotation entity = new Quotation();
        entity.setCustomerName(record.getName());
        List<Tag> citiesInDb = em.createQuery("SELECT c FROM Tag c WHERE c.name=:name", Tag.class).setParameter("name", record.getCity()).getResultList();
        Tag tag = resolveCity(record, citiesInDb);
        entity.setTag(tag);
        return entity;
    }

    // TODO optimize: map + em.getReference
    private Tag resolveCity(QuotationRecord record, List<Tag> citiesInDb) {
        if (citiesInDb.isEmpty()) {
            Tag tag = new Tag(record.getCity());
            em.persist(tag);
            return tag;
        } else if (citiesInDb.size() == 1) {
            return citiesInDb.get(0);
        } else {
            throw new IllegalStateException("Duplicate country found in DB: " + record.getCity());
        }
    }
}
