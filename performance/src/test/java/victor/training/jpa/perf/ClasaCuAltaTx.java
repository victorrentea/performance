package victor.training.jpa.perf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Component
@Slf4j
public class ClasaCuAltaTx {
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int anotherMethod(Collection<Parent> parents) {
        log.debug("Start iterating over {} parents: {}", parents.size(), parents);
        int total = 0;
        for (Parent parent : parents) {
            log.debug("Oare hehehe ce set e ala ? " + parent.getChildren().getClass());
            total += parent.getChildren().size();
        }
        log.debug("Done counting: {} children", total);
        return total;
    }
}
