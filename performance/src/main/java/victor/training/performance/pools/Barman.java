package victor.training.performance.pools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static victor.training.performance.ConcurrencyUtil.sleep2;

@Service
@Slf4j
public class Barman {
    public Beer pourBeer() {
        log.debug("Pouring Beer to ...");
        sleep2(1000);
        return new Beer();
    }

    public Vodka pourVodka() {
        log.debug("Pouring Vodka...");
        sleep2(1000);
        return new Vodka();
    }
}
