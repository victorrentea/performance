package victor.training.performance.pools;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Data
@Service
@Scope(scopeName = "thread", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyRequestContext {
    private String currentUser;
    private String requestId;
}
// in unit tests you replace this bean definition with a singleton.
// assuming yore' not running those tests in parallel.
    // failsafe plugin :
