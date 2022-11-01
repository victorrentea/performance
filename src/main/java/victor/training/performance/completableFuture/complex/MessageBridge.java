package victor.training.performance.completableFuture.complex;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@RestController
public class MessageBridge {

    private final Map<String, CompletableFuture<String>> pendingRequests = Collections.synchronizedMap(new HashMap<>());


    @GetMapping("message-bridge")
    public CompletableFuture<String> getViaTimedBuffer() {
        String requestId = UUID.randomUUID().toString();
        String requestPayload = RandomStringUtils.randomAlphabetic(20);

        CompletableFuture<String> replyFuture = new CompletableFuture<>();
        pendingRequests.put(requestId, replyFuture);
        log.info("map size is now " + pendingRequests.size());
        sendRequestMessage(requestId, requestPayload);
        return replyFuture
                .orTimeout(3, TimeUnit.SECONDS)
                .thenApply(replyPayload -> "Sent request id=" + requestId + ", payload=" + requestPayload + "<br>\nGot response: " + replyPayload)
                .exceptionally(e -> {
                    if (e.getCause() instanceof TimeoutException) {
                        pendingRequests.remove(requestId);
                        return "Your order ID "+ requestId+" is in progress";
                    } else {
                        throw new CompletionException(e);
                    }
                })
                ;
    }

    // fake a message system
    private void sendRequestMessage(String requestId, String requestPayload) {
        boolean willTimeout = Math.random() < 0.5;
        Instant replyTime = Instant.now().plusSeconds(willTimeout ? 1 : 5);
        scheduler.schedule(() -> handleMessage(requestId, "Response to " + requestPayload), replyTime);
    }

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    public void handleMessage(String inReplyToHeader, String responseMessageBody) {
        CompletableFuture<String> replyFuture = pendingRequests.remove(inReplyToHeader);
        if (replyFuture == null) {
            log.warn("Reply message came to late to request id: " + inReplyToHeader);
            return;
        }
        replyFuture.complete(responseMessageBody);
    }

}
