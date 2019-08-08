package victor.proxy;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import victor.proxy.beanpostprocessor.ExpensiveOps_CachedViaBPP;
import victor.proxy.cacheable.ExpensiveOps_Cacheable;
import victor.proxy.classproxy.ExpensiveOps_ClassProxied;
import victor.proxy.classproxy.ExpensiveOps_ClassProxy;
import victor.proxy.decorator.ExpensiveOps_Decorated;
import victor.proxy.decorator.ExpensiveOps_Decorator;
import victor.proxy.decorator.IExpensiveOps_Decorated;
import victor.proxy.interfaceproxy.ExpensiveOps_Proxied;
import victor.proxy.interfaceproxy.ExpensiveOps_Proxy;
import victor.proxy.interfaceproxy.IExpensiveOps_Proxied;
import victor.proxy.method.ExpensiveOps_CacheMethod;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
//@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
//@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
//@Fork(1)
public class ProxyPerformanceTest {

	
	private ExpensiveOps_CacheMethod cacheMethod = new ExpensiveOps_CacheMethod();
	@Benchmark
	public void cacheMethod() {
		for (int i = 0; i< 10000; i++) cacheMethod.isOdd(i);
	}
	
	private IExpensiveOps_Decorated decorated = new ExpensiveOps_Decorator(new ExpensiveOps_Decorated());
	@Benchmark
	public void decorator() {
		for (int i = 0; i< 10000; i++) decorated.isOdd(i);
	}
	
	private IExpensiveOps_Proxied interfaceProxy = ExpensiveOps_Proxy.proxy(new ExpensiveOps_Proxied());
	@Benchmark
	public void interfaceProxy() {
		for (int i = 0; i< 10000; i++) interfaceProxy.isOdd(i);
	}
	
	private ExpensiveOps_ClassProxied classProxy = ExpensiveOps_ClassProxy.proxy(new ExpensiveOps_ClassProxied());
	@Benchmark
	public void classProxy() {
		for (int i = 0; i< 10000; i++) classProxy.isOdd(i);
	}
	
	static ConfigurableApplicationContext context;
    @Setup (Level.Trial) 
    public synchronized void  initialize() {
        try {
            String args = "";
            if(context == null) {
                context = SpringApplication.run(PerfProxySpringApp.class, args );
            }
            springCacheable = context.getBean(ExpensiveOps_Cacheable.class);
            springCachedViaBPP = context.getBean(ExpensiveOps_CachedViaBPP.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private ExpensiveOps_CachedViaBPP springCachedViaBPP;
	@Benchmark
	public void springCachedViaBPP() {
		for (int i = 0; i< 10000; i++) springCachedViaBPP.isOdd(i);
	}

    private ExpensiveOps_Cacheable springCacheable;
	@Benchmark
	public void springCacheable() {
		for (int i = 0; i< 10000; i++) springCacheable.isOdd(i);
	}

	
	
	 public static void main(String[] args) throws RunnerException {
	        Options opt = new OptionsBuilder()
	                .include(ProxyPerformanceTest.class.getSimpleName())
//	                .forks(1)
	                .build();

	        new Runner(opt).run();
	    }

	
}
