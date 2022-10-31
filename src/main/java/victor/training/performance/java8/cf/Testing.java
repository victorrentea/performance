package victor.training.performance.java8.cf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

@RestController
public class Testing {
    private final TestingDependency dependency;

    public Testing(TestingDependency dependency) {
        this.dependency = dependency;
    }
    @GetMapping
    public CompletableFuture<AB> methodToTest(@RequestParam(defaultValue = "1") String id) {
        return dependency.apiACall(id)
                .exceptionally(ex -> {
                    if (ex instanceof HttpClientErrorException.NotFound) {
                        return new A("Not Found");
                    } else {
                        throw new CompletionException(ex);
                    }
                })
                .completeOnTimeout(new A("Timeout"), 500, TimeUnit.MILLISECONDS)
                .thenCompose(a -> a.getA().equals("SOLO") ?
                        CompletableFuture.completedFuture(new AB(a, null)) :
                        dependency.apiBCall(id).thenApply(b -> new AB(a, b)));
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class A {
    String a;
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class B {
    String b;
}
@Data
class AB{
    private final A a;
    private final B b;
}
@Component
class TestingDependency {
    public CompletableFuture<A> apiACall(String id) {
        return new AsyncRestTemplate()
                .getForEntity("http://localhost:9999/api/a/{id}", A.class, id)
                .completable()
                .thenApply(HttpEntity::getBody);
    }
    public CompletableFuture<B> apiBCall(String id) {
        return new AsyncRestTemplate()
                .getForEntity("http://localhost:9999/api/b/{id}", B.class, id)
                .completable()
                .thenApply(HttpEntity::getBody);
    }
}
