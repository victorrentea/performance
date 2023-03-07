package victor.training.performance.jpa;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.UUID;

public class UUIDGenerator implements IdentifierGenerator {
   private static final Logger log = LoggerFactory.getLogger(UUIDGenerator.class);

   @Override
   public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
      log.debug("Generating ID for " + object.getClass());
      return UUID.randomUUID().toString();
   }
}
