package victor.training.performance.leak;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.CacheService.InvoiceParams;
import victor.training.performance.leak.obj.Big20MB;
import victor.training.performance.util.PerformanceUtil;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.synchronizedMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static victor.training.performance.util.PerformanceUtil.done;

@RestController
@RequestMapping("leak12")
@RequiredArgsConstructor
public class Leak12_Caching {
  private final CacheService cacheService;

  @GetMapping
  public String key(@RequestParam(required = false) LocalDate date) {
    if (date == null) {
      date = LocalDate.now();
    }
    Big20MB data = cacheService.getTodayFex(date);
    return "Data from cache for today = " + data + ", " + PerformanceUtil.getUsedHeapPretty() + "<br>" +
           "also try Jan " +
           range(1, 30).mapToObj("<a href='leak12?date=2025-01-%1$02d'>%1$s</a>, "::formatted).collect(joining()) +
           "<p>should be in <a href='/actuator/prometheus' target='_blank'>metrics</a>" +
           done();
  }

  @GetMapping("signature")
  public String signature() {
    long requestTime = System.currentTimeMillis();
    Big20MB data = cacheService.getContractById(1L, requestTime);
    return "Contract id:1 = " + data + ", " + PerformanceUtil.getUsedHeapPretty();
  }

  @GetMapping("objectKey")
  public String objectKey() {
    UUID contractId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    Big20MB data = cacheService.getInvoice(new InvoiceParams(contractId, 2023, 10));
    return "Invoice = " + data + ", " + PerformanceUtil.getUsedHeapPretty();
  }

  @GetMapping("mutableKey")
  public String mutable() {
    Big20MB data = cacheService.inquiry(new Inquiry().setYearValue(2025).setMonthValue(10));
    return "Invoice = " + data + ", " + PerformanceUtil.getUsedHeapPretty();
  }
}

@Service
@Slf4j
@RequiredArgsConstructor
class CacheService {
  private final InquiryRepo inquiryRepo;

  private Big20MB fetchData() {
    return new Big20MB();
  }

  // === ❌Anti-Pattern: Manual Cache ===
  Map<LocalDate, Big20MB> fexCache = synchronizedMap(new HashMap<>());

  public Big20MB getTodayFex(LocalDate date) {
    return fexCache.computeIfAbsent(date, d -> {
      log.debug("Fetch data for date: {}", date);
      return fetchData();
    });
  }

  @Bean
  MeterBinder fexCacheMetrics() {// 🔔ALARM on this
    return registry -> Gauge.builder("fex_cache_size", fexCache, Map::size).register(registry);
  }

  // === ❌ Cache Key Mess-up #1 ===

  //  @Cacheable proxy returns the previously returned value for the same parameter(s)
  @Cacheable("signature")
  public Big20MB getContractById(Long contractId, long requestTime) {
    log.debug("Fetch contract id={} at {}", contractId, requestTime);
    return fetchData();
  }


  // === ❌ Cache Key Mess-up #2 ===

  @RequiredArgsConstructor
  @Getter
  @Setter
  static class InvoiceParams {
    private final UUID contractId;
    private final int year;
    private final int month;
  }
  @Cacheable("invoices")
  public Big20MB getInvoice(InvoiceParams params) {
    log.debug("Fetch invoice for {} {} {}", params.getContractId(), params.getYear(), params.getMonth());
    return fetchData();
  }

  // === ❌ Cache Key Mess-up #3 ===

  private final CacheManager cacheManager;

  public Big20MB inquiry(Inquiry param) {
    return cacheManager.getCache("inquiries") // ≈ @Cacheable("inquiries")
        .get(param, () -> {
          inquiryRepo.save(param);
          return fetchData();
        });
  }
}

@Data
@Entity
class Inquiry {
  @GeneratedValue
  @Id
  Long id;
  UUID contractId;
  int yearValue;
  int monthValue;
}

interface InquiryRepo extends JpaRepository<Inquiry, Long> {
}


/**
 * ⭐️ KEY POINTS
 * ☣️ @Cacheable can be too magic
 * 👍 write automated @Tests for cache use
 * 👍 monitor+alarm on prod cache hit/miss ratio
 * 👍 non-primitive key should be immutable + hashCode/equals (record💖)
 */