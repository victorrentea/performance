package victor.proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import lombok.extern.slf4j.Slf4j;
import victor.proxy.beanpostprocessor.ExpensiveOps_CachedViaBPP;
import victor.proxy.beanpostprocessor.ExpensiveOps_CachedViaBPP_ClassProxy;

@Slf4j
@EnableAspectJAutoProxy 
@EnableCaching 
@SpringBootApplication
public class PerfProxySpringApp {
	@Bean
	public BeanPostProcessor weaveCache() {
		return new BeanPostProcessor() {
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof ExpensiveOps_CachedViaBPP) {
					return ExpensiveOps_CachedViaBPP_ClassProxy.proxy((ExpensiveOps_CachedViaBPP) bean);
				} else {
					return bean;
				}
			}
		};
	}
}
