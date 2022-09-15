package victor.training.performance.util;

import org.hibernate.EmptyInterceptor;

public class SimulateNetworkDelayHibernateInterceptor extends EmptyInterceptor {
    @Override
    public String onPrepareStatement(String sql) {
        PerformanceUtil.sleepq(5);
        return sql;
    }
}