package victor.training.concurrency.java8;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.management.RuntimeErrorException;

import lombok.ToString;
import victor.training.concurrency.ConcurrencyUtil;

public class BarApp {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		log("Am venit la bar");
		
		// DefferedResult / promise al java: 

		CompletableFuture<Bere> futureBere = supplyAsync(Barman::toarnaBere);		// FORK
		CompletableFuture<Whiskey> futureWhiskey = supplyAsync(Barman::toarnaWhiskey);// FORK
		CompletableFuture<Pastrama> futurePastrama = supplyAsync(() -> Frigider.maDucSaIauPastrama()); // FORK
		
		CompletableFuture<RachetaCoktail> futureCocktail = futureWhiskey.thenCombine(futureBere, 
				RachetaCoktail::new);
		
		
		futurePastrama.thenAcceptBoth(futureCocktail, (pastrama,cocktail) -> {
			log("Mananc " + pastrama);
			log("Beu " + cocktail);
			log("Plec acasa");		
		});
		
		
		
		log("gata use-caseul. ma intorc in piscina. Pleoshc!");
		sleep2(4000); // intr-o aplicatie normala, app nu moare dupa executia unui use-case.
		// AICI moare, pentru ca threadurile pe care ruleaza procesarile sunt daemoni care nu tin app in viata.
	}

}

@ToString
class RachetaCoktail {
	private final Whiskey whiskey;
	private final Bere bere;
	public RachetaCoktail(Whiskey whiskey, Bere bere) {
		this.whiskey = whiskey;
		this.bere = bere;
		log("Amestec " + bere + " cu " + whiskey);
		sleep2(2000);
		log("Gata cocktailul");
	}
}
@ToString
class Whiskey {}
@ToString
class Bere {}
@ToString
class Pastrama {}

class Barman {
	public static Bere toarnaBere() {
		log("Torn bere");
		sleep2(1000);
		log("Gata");
		return new Bere();
	}
	public static Whiskey toarnaWhiskey() {
		log("Torn Whiskey");
		sleep2(500);
		log("Gata");
		return new Whiskey();
	}
	
}

class Frigider {
	public static Pastrama maDucSaIauPastrama() {
		log("Merg si ma gandesc: Grasimea imi va proteja stomacul si nu voi instra in coma alcoolica.");
		sleep2(2000);
		log("Gata");
		return new Pastrama();
	}
}