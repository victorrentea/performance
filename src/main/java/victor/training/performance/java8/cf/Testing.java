package victor.training.performance.java8.cf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
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
    public CompletableFuture<XY> methodToTest(@RequestParam(defaultValue = "1") String id) {
        return dependency.apiACall(id)
                .exceptionally(ex -> {
                    if (ex instanceof HttpClientErrorException.NotFound) {
                        return new X("Not Found");
                    } else {
                        throw new CompletionException(ex);
                    }
                })
                .completeOnTimeout(new X("Timeout"), 500, TimeUnit.MILLISECONDS)
                .thenCompose(x -> x.getX().equals("SOLO") ?
                        CompletableFuture.completedFuture(new XY(x, null)) :
                        dependency.apiBCall(id).thenApply(y -> new XY(x, y)));
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class X {
    String x;
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class Y {
    String y;
}
@Data
class XY {
    private final X x;
    private final Y y;
}
@Component
class TestingDependency {
    public CompletableFuture<X> apiACall(String id) {
        return new AsyncRestTemplate()
                .getForEntity("http://localhost:9999/api/a/{id}", X.class, id)
                .completable()
                .thenApply(HttpEntity::getBody);
    }
    public CompletableFuture<Y> apiBCall(String id) {
        return new AsyncRestTemplate()
                .getForEntity("http://localhost:9999/api/b/{id}", Y.class, id)
                .completable()
                .thenApply(HttpEntity::getBody);
    }
}
