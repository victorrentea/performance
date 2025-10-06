package victor.training.performance.interview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class Logging {
  public static void main(String... args) throws JsonProcessingException {
    List<Point> list = IntStream.range(0, 1000000)
        .mapToObj(i -> new Point(1, 2))
        .toList();

    log.trace("Hello " + list.size() + "."); // ✅ Concatenating 3 strings = super-fast

    log.trace("Hello " + list); // ❌ list.toString() called even if TRACE is not enabled
    log.trace("Hello {}", list); // ✅

    log.trace("Hello {}", toJson(list)); // ❌ toJson called even if TRACE is not enabled
    log.atTrace().log(() -> "Hello " + toJson(list)); // ✅
    if (log.isTraceEnabled()) log.trace("Hello {}", toJson(list)); // ✅
  }

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static String toJson(List<Point> list) {
    try {
      return objectMapper.writeValueAsString(list);
    } catch (JsonProcessingException e) {
      return "<cannot-serialize-json>";
    }
  }
}
