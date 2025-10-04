package victor.training.performance.leak.threadscope;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Data
@Service
@Scope(scopeName = "thread", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScoped {
    private String currentUser;
    private String requestId;
}
