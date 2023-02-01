package victor.training.performance.spring;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.spring.GDPRFilter.VisibleFor;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

@RestController
@Slf4j
public class Profile11_ChainedNetworkCalls {
  @Autowired
  private TicketRepo repo;
  @Autowired
  private RestTemplate rest;

  @Value
  private static class TicketDto {
    Long id;
    @VisibleFor("admin")
    String creator;
    String assigned;
  }

  @GetMapping("{id}")
  @Timed
  public TicketDto getById(@PathVariable Long id) throws InterruptedException {
    Ticket ticket = repo.findById(id).orElseThrow();

    //  🛑 Not monitoring network call durations  ~> use @Timed on a method called via a Spring proxy
    //  🛑 Calling same remote endpoint multiple times/use-case ~> send a bulk request (requires new API)
    String creator = rest.getForObject("http://localhost:9999/get-one/" + ticket.getCreatorUserId(), String.class);
    String assigned = rest.getForObject("http://localhost:9999/get-one/" + ticket.getAssignedUserId(), String.class);

//    List<String> list = someApiClient.fetchMany(List.of(ticket.getCreatorUserId(), ticket.getAssignedUserId()));
//    String creator = list.get(0);
//    String assigned = list.get(1);
    return new TicketDto(ticket.getId(), creator, assigned);
  }

  @Autowired
  private SomeApiClient someApiClient;

  @Component
  public static class SomeApiClient {
    @Autowired
    private RestTemplate rest;
    @Autowired
    MeterRegistry reg;

    @Timed
    public List<String> fetchMany(List<Long> ids) {
      String idCsv = ids.stream().map(Objects::toString).collect(Collectors.joining(","));
      log.info("Sending a bulk request for all ids: " + ids);
      Timer timer = reg.timer("custommetric");
      long t0 = currentTimeMillis();
      List<String> bulkResponse = rest.getForObject("http://localhost:9999/get-many?ids=" + idCsv, List.class);
      long t1 = currentTimeMillis();
      timer.record(t1-t0, TimeUnit.MILLISECONDS);
      log.info("Got bulk response: " + bulkResponse);
      return bulkResponse;
    }
  }
}
