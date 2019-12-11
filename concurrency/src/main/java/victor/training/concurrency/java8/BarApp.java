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
		CompletableFuture<Bere> futureBere = supplyAsync(() -> Barman.toarnaBere());
		CompletableFuture<Whiskey> futureWhiskey = supplyAsync(() -> Barman.toarnaWhiskey());
		
		
		CompletableFuture<Void> ambeleGata = allOf(futureBere, futureWhiskey); // Operatorul JOIN in UML
		CompletableFuture<RachetaCoktail> futureCocktail = 
				ambeleGata.thenApply(new Function<Void, RachetaCoktail>() {
			@Override
			public RachetaCoktail apply(Void v) {
				try {
					Bere bere = futureBere.get(); // NU VA BLOCA DE LOC: berea va fi fost deja turnata
					Whiskey whiskey = futureWhiskey.get();
					return new RachetaCoktail(whiskey, bere);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		//	futureBere.then
		
		Pastrama pastrama = Frigider.maDucSaIauPastrama();
		log("Mananc " + pastrama);
		
		
		
		RachetaCoktail coktail = futureCocktail.get();
		log("Beu " + coktail);
		
		log("Plec acasa");		
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