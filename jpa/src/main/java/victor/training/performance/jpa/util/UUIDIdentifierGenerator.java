package victor.training.performance.jpa.util;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.UUID;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Slf4j
public class UUIDIdentifierGenerator implements IdentifierGenerator {

   @IdGeneratorType(UUIDIdentifierGenerator. class)
   @Retention(RUNTIME)
   @Target({METHOD,FIELD})
   public @interface GeneratedUUID {
   }

   @Override
   public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
      log.debug("Generating ID for " + object.getClass());
      return UUID.randomUUID().toString();
   }
}
