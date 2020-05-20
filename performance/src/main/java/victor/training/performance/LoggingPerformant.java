package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
public class LoggingPerformant {

    public static void main(String[] args) {

    }

    public void m(Long id, List<Date> dates) { // 50K de date
        log.debug("Executing for id={}", id);

        // sau, mai rar:
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (log.isDebugEnabled()) {
            log.debug("Executing for id={}, dates={}", id, dates.stream().map(sdf::format).collect(toList()));
        }
    }


}
