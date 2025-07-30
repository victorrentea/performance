package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.BigObject20MB;
import victor.training.performance.util.PerformanceUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@RestController
@RequestMapping("leak7")
public class Leak7_Cache {
   @Autowired
   private CacheService cacheService;

   @GetMapping
   public String cacheKey() {
      BigObject20MB data = cacheService.getCachedDataForDay(LocalDate.now());
      return "Data from cache for today = " + data + ", " + PerformanceUtil.getUsedHeap();
   }

   @GetMapping("signature")
   public String signature() {
      long requestStartTime = System.currentTimeMillis();
      BigObject20MB data = cacheService.getContractById(1L , requestStartTime);
      return "Contract id:1 = " + data + ", " + PerformanceUtil.getUsedHeap();
   }

   @GetMapping("customKey")
   public String customKey() {
      BigObject20MB data = cacheService.getInvoiceByContractAndDate(new InvoiceByDate(13L, 2023, 10));
      return "Invoice = " + data + ", " + PerformanceUtil.getUsedHeap();
   }
}

@Service
@Slf4j
class CacheService {
   // @Cacheable makes a proxy intercept the method call and return
   // the previously cached value for that parameter (if any)
   @Cacheable("day-cache") // ~static HashMap<LocalDateTime, ...> = infinite no of keys
   // #1: cache hit ratio = 0% on grafana (metrics)
   // #2: OOME if not bounded
   public BigObject20MB getCachedDataForDay(LocalDate date) {
      log.debug("Fetch data for date: {}", date.format(DateTimeFormatter.ISO_DATE));
      return new BigObject20MB();
   }

   @Cacheable(value = "contracts",key = "#contractId")
   public BigObject20MB getContractById(Long contractId, long requestStartTime) {
      log.debug("<{}> Fetch contract id={}", requestStartTime, contractId);
      return new BigObject20MB();
   }

   @Cacheable("invoices")
   public BigObject20MB getInvoiceByContractAndDate(InvoiceByDate param) {
//      if (cache.get(...)) //perhaps is better to avoid AOP @, and programatically use caches
//      ..
      log.debug("Fetch invoice for {}", param);
      return new BigObject20MB();
   }
}

class InvoiceByDate {
   private final Long contractId;
   private final int year;
   private final int month;

   InvoiceByDate(Long contractId, int year, int month) {
      this.contractId = contractId;
      this.year = year;
      this.month = month;
   }

   public int getMonth() {
      return month;
   }

   public int getYear() {
      return year;
   }

   public Long getContractId() {
      return contractId;
   }

   @Override
   public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      InvoiceByDate that = (InvoiceByDate) o;
      return year == that.year && month == that.month && Objects.equals(contractId, that.contractId);
   }

   @Override
   public int hashCode() {
      return Objects.hash(contractId, year, month);
   }
}

/**
 * KEY POINTS
 * - [ideally] test that your caches work via automated tests (eg @SpringBootTest)
 */