package victor.training.concurrency;

import static victor.training.concurrency.ConcurrencyUtil.log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

// V1
//		log("START");
//		A();
//		ExecutorService pool = Executors.newFixedThreadPool(2);
//		Future<?> futureB = pool.submit(InlantuireDeApeluriAsincrone::B);
//		Future<?> futureC = pool.submit(InlantuireDeApeluriAsincrone::C);
//		
//		futureB.get(); 
//		futureC.get();
//		
//		D();
//		log("GATA");
//		pool.shutdown();
public class InlantuireDeApeluriAsincrone {
	public static void main(String[] args) throws InterruptedException, ExecutionException {

		log("START");
		
		Executor myOwnExecutor = new ForkJoinPool();
		CompletableFuture<Void> aPromise = CompletableFuture.runAsync(
				InlantuireDeApeluriAsincrone::A, myOwnExecutor);
		
		CompletableFuture.allOf(
				aPromise.thenRunAsync(InlantuireDeApeluriAsincrone::B), 
				aPromise.thenRunAsync(InlantuireDeApeluriAsincrone::B), 
				aPromise.thenRunAsync(InlantuireDeApeluriAsincrone::B), 
				aPromise.thenRunAsync(InlantuireDeApeluriAsincrone::B), 
				aPromise.thenRunAsync(InlantuireDeApeluriAsincrone::C))
			.thenRunAsync(InlantuireDeApeluriAsincrone::D)
			.thenRunAsync(() -> log("PasulE - IO Intensive"), myOwnExecutor)
			.thenRunAsync(() -> log("PasulF"))
			.thenRunAsync(() -> log("PasulG"))
			.thenRunAsync(() -> log("PasulH"))
			.get();

//		dPromise.get();
		
		log("GATA");
	}
	
	public static void A() {
		log("Start A");
		ConcurrencyUtil.sleep2(1000);
		log("End A");
	}
	public static void B() {
		log("Start B");
		ConcurrencyUtil.sleep2(1000);
		log("End B");
	}
	public static void C() {
		log("Start C");
		ConcurrencyUtil.sleep2(1000);
		log("End C");
	}
	public static void D() {
		log("Start D");
		ConcurrencyUtil.sleep2(1000);
		log("End D");
	}
}
