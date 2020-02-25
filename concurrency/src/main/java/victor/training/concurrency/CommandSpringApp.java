package victor.training.concurrency;

import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

@Slf4j
public class CommandSpringApp {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        unRequest();
        log("Gata requestul. Threadul asta se intoarce la joaca in Piscina");
        sleep2(4000);
    }

    private static void unRequest() throws InterruptedException, ExecutionException {
        log("Submitting my order");
        Barman barman = new Barman();

        CompletableFuture<Beer> futureBeer = supplyAsync(() -> barman.pourBeer("bruna"))
                .exceptionally(e -> barman.pourBeer("blonda"));
        CompletableFuture<Vodka> futureVodka = supplyAsync(barman::pourVodka);
        log("Waiting for my drinks...");

//        CompletableFuture.allOf(futureBeer, futureVodka)
//                .thenRun(() -> log("AU venit!"));

        CompletableFuture<Void> futureBaut = futureBeer
                .thenCombine(futureVodka, DillyDilly::new)
                .thenAccept(CommandSpringApp::beu)
                .exceptionally(e -> {
                    log("Beau un shot");
                    return null;
                });

        futureBaut.thenRunAsync(CommandSpringApp::sendSmsTemplate);
        futureBaut.thenRunAsync(CommandSpringApp::mergAcasa);
    }

    private static void sendSmsTemplate() {
        log("SMS: writing...");
        sleep2(1000);
        log("SMS: Am terminat livrarea. Vin acasa.");
    }

    private static void mergAcasa() {
        log("Chem taxi");
        sleep2(1000);
        log("Ding-dong");
    }

    private static void beu(DillyDilly dilly) {
        log("Beau " + dilly);
        sleep2(1000);
        log("Hic. Gata!");
    }
}

@Value
class DillyDilly {
    Beer beer;
    Vodka vodka;
}

class Barman {
    public Beer pourBeer(String tip) {
        log("Pouring Beer...");
        sleep2(1000);
        if (tip == "bruna") {
            throw new IllegalStateException("Butoi gol");
        }
        return new Beer(tip);
    }

    public Vodka pourVodka() {
        log("Pouring Vodka...");
        sleep2(1000);
        return new Vodka();
    }
}

@Data
class Beer {
    private final String tip;
}

@Data
class Vodka {
}
