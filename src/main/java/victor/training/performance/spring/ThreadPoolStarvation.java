package victor.training.performance.spring;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@RestController
public class ThreadPoolStarvation {
    @GetMapping("fast")
    public String fast() {
        return "fast";
    }

    // sync
    @GetMapping("profile")
    public String profile() {
        return fetchExp() + fetchItems();
    }
    public String fetchExp() {
        return new RestTemplate().getForObject("http://localhost:9999/exp", String.class);
    }
    public String fetchItems() {
        return new RestTemplate().getForObject("http://localhost:9999/items", String.class);
    }


    // ===== async
    @GetMapping("profile-async")
    public CompletableFuture<String> profileAsync() {
        return fetchExpAsync().thenCombine(fetchItemsAsync(), (e, i)-> e + i);
    }

    public CompletableFuture<String> fetchExpAsync() {
        return new AsyncRestTemplate().getForEntity("http://localhost:9999/exp", String.class).completable().thenApply(HttpEntity::getBody);
    }
    public CompletableFuture<String> fetchItemsAsync() {
        return new AsyncRestTemplate().getForEntity("http://localhost:9999/items", String.class).completable().thenApply(HttpEntity::getBody);
    }
}
