package victor.training.performance.leak;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.CacheService.InvoiceByDate;
import victor.training.performance.leak.obj.Big20MB;
import victor.training.performance.util.PerformanceUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("leak27")
@RequiredArgsConstructor
public class Leak27_Caching {
  private final CacheService cacheService;

  @GetMapping
  public String key() {
    Big20MB data = cacheService.getTodayFex(LocalDateTime.now());
    return "Data from cache for today = " + data + ", " + PerformanceUtil.getUsedHeapPretty();
  }

  @GetMapping("signature")
  public String signature() {
    long requestStartTime = System.currentTimeMillis();
    Big20MB data = cacheService.getContractById(1L, requestStartTime);
    return "Contract id:1 = " + data + ", " + PerformanceUtil.getUsedHeapPretty();
  }

  @GetMapping("objectKey")
  public String objectKey() {
    Big20MB data = cacheService.getInvoice(new InvoiceByDate(null, 2023, 10));
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

  // Note: @Cacheable proxy intercepts the call and
  // returns the previously returned value for the same parameter(s)
  @Cacheable("fex-cache")
  public Big20MB getTodayFex(LocalDateTime date) {
    log.debug("Fetch data for date: {}", date.format(DateTimeFormatter.ISO_DATE));
    return fetchData();
  }

  @Cacheable("signature")
  public Big20MB getContractById(Long contractId, long requestStartTime/*added*/) {
    log.debug("Fetch contract id={} at {}", contractId, requestStartTime);
    return fetchData();
  }

  @Getter
  @Setter
  static class InvoiceByDate {
    private UUID contractId;
    private int year;
    private int month;

    InvoiceByDate(UUID contractId, int year, int month) {
      this.contractId = contractId;
      this.year = year;
      this.month = month;
    }
  }

  private final CacheManager cacheManager;

  @Cacheable("invoices")
  public Big20MB getInvoice(InvoiceByDate param) {
    // 'extracted parameters to a class' - commit message by @vibe_coder
    log.debug("Fetch invoice for {}", param);
    return fetchData();
  }

  public Big20MB inquiry(Inquiry param) {
    return cacheManager.getCache("inquiries")
        .get(param, () -> { // instead of @Cacheable("inquiries")
          inquiryRepo.save(param);
          return fetchData();
        });
  }
}

@Data
@Entity
class Inquiry {
  @Id
  @GeneratedValue
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