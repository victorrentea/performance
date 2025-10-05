package victor.training.performance.leak;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.CacheService.InvoiceParams;
import victor.training.performance.leak.obj.Big20MB;
import victor.training.performance.util.PerformanceUtil;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("leak12")
@RequiredArgsConstructor
public class Leak12_Caching {
  private final CacheService cacheService;

  @GetMapping
  public String key() {
    Big20MB data = cacheService.getTodayFex(LocalDateTime.now());
    return "Data from cache for today = " + data + ", " + PerformanceUtil.getUsedHeapPretty();
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
  private Big20MB fetchData() {
    return new Big20MB();
  }

  // Note: @Cacheable proxy intercepts the call and
  // returns the previously returned value for the same parameter(s)
  @Cacheable("fex-cache")
  public Big20MB getTodayFex(LocalDateTime date) {
    log.debug("Fetch data for date: {}", date);
    return fetchData();
  }

  @Cacheable("signature")
  public Big20MB getContractById(Long contractId, long requestTime) {
    log.debug("Fetch contract id={} at {}", contractId, requestTime);
    return fetchData();
  }

  private final CacheManager cacheManager;

  @RequiredArgsConstructor
  @Getter
  @Setter
  static class InvoiceParams{
    private final UUID contractId;
    private final int year;
    private final int month;
  }

  @Cacheable("invoices")
  public Big20MB getInvoice(InvoiceParams params) {
    log.debug("Fetch invoice for {} {} {}", params.getContractId(), params.getYear(), params.getMonth());
    return fetchData();
  }

  public Big20MB inquiry(Inquiry param) {
    return cacheManager.getCache("inquiries") // ≈ @Cacheable("inquiries")
        .get(param, () -> fetchData());
  }
}

@Data
class Inquiry {
  Long id;
  UUID contractId;
  int yearValue;
  int monthValue;
}


/**
 * ⭐️ KEY POINTS
 * ☣️ @Cacheable can be too magic
 * 👍 write automated @Tests for cache use
 * 👍 monitor+alarm on prod cache hit/miss ratio
 * 👍 non-primitive key should be immutable + hashCode/equals (record💖)
 */