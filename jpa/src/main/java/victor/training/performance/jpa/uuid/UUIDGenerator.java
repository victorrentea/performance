package victor.training.performance.jpa.uuid;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.UUID;

@Slf4j
public class UUIDGenerator implements IdentifierGenerator {

   @Override
   public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
      log.debug("Generating ID for " + object.getClass());
      return UUID.randomUUID().toString();
   }
}
