package victor.training.performance.leak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("leak18")
// from https://www.amazon.com/Release-Production-Ready-Software-Pragmatic-Programmers/dp/0978739213
public class Leak18_ConnectionLeak {
  private final DataSource dataSource;

  public record Flight(
      String origin,
      String destination
  ) {}

  @GetMapping
  public void endpoint(Flight flight) throws SQLException {
    var connection = dataSource.getConnection();
    log.info("Start work on ✈️{}->{}",
        flight.origin.toUpperCase(),
        flight.destination.toUpperCase());
    // real work
    connection.close();
  }
}
