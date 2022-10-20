package victor.training.performance.primitives;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@SpringBootApplication
@RestController
public class InchidereMagazin {


    public static void main(String[] args) {
        SpringApplication.run(InchidereMagazin.class, args);
    }

    private CompletableFuture<String> userInputFuture = new CompletableFuture<>();
    private CompletableFuture<String> closePromise;


    @PostConstruct
    public void laStartup() {
        log.info("Fire in the hole CS");
        CompletableFuture.anyOf(
                        supplyAsync(() -> "T", delayedExecutor(5, SECONDS)),
                        userInputFuture
                )
                .thenAccept(status -> dupa((String) status));
    }

    //indiferent daca a dat timeout(status="T") sau nu (status=event).
    public void dupa(String status) {
        userInputFuture.cancel(true);
        log.info("Inchid cu status:  " + status);
    }

    @GetMapping("user")
    public void userInput() {
        String userInput = "user input";
        log.info("event " + userInput);
        userInputFuture.complete(userInput);
    }


}
