package victor.training.spring.batch.core;

import org.hibernate.EmptyInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import victor.training.spring.batch.util.PerformanceUtil;

@Component
public class SimulateNetworkDelayHibernateInterceptor extends EmptyInterceptor {

  private static final Logger log = LoggerFactory.getLogger(SimulateNetworkDelayHibernateInterceptor.class);
  public static int MILLIS = 0;

  @EventListener(ApplicationStartedEvent.class)
  public void setNetworkDelay() {
    log.info("Adding 5ms delay/sql, to simulate real life");
    MILLIS = 5;
  }

  @Override
  public String onPrepareStatement(String sql) {
    if (MILLIS != 0)
      PerformanceUtil.sleepMillis(MILLIS);
    return sql;
  }
}