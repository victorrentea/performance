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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("leak7")
public class Leak7_Cache {
   @Autowired
   private CacheService cacheService;

   @GetMapping
   public String cacheKey() {
      BigObject20MB data = cacheService.getCachedDataForDay(LocalDateTime.now());
      return "Data from cache for today = " + data + ", " + PerformanceUtil.getUsedHeap();
   }

   @GetMapping("signature")
   public String signature() {
      long requestStartTime = System.currentTimeMillis();
      BigObject20MB data = cacheService.getContractById(1L, requestStartTime);
      return "Contract id:1 = " + data + ", " + PerformanceUtil.getUsedHeap();
   }

   @GetMapping("customKey")
   public String customKey() {
      BigObject20MB data = cacheService.getInvoiceByContractAndDate(new InvoiceByDate(13L, 2023, 10));
      return "Invoice = " + data + ", " + PerformanceUtil.getUsedHeap();
   }

//   Map<K,Entry{Value,Timestamp}> cache;
   // get(key) {
   //   entry = cache.get(key)
   //   if (entry.timestamp < now - 1h) { // you might return data 1h old
   //     return entry.value
   //   } else {
   //     cache.remove(key); //tentative fix. wrong. keys no one asked for again are NEVER removed
   //     real solution : have a timer watchdog looking in the cache eg every minute to remove the expired entries
   //   }
   //   v = remoteApi.call(k);
   //   cache.put(key, new Entry(v, now)); // inserts or updates in the cache
   //   return v;
   // }
   // 1 week later the map had 1GB containin ALL their JDs
   // on expiration I don't remove the obsolette expired entry from the map

}

@Service
@Slf4j
class CacheService {
   // @Cacheable makes a proxy intercept the method call and return
   // the previously cached value for that parameter (if any)
   @Cacheable("day-cache")
   public BigObject20MB getCachedDataForDay(LocalDateTime date) {
      log.debug("Fetch data for date: {}", date.format(DateTimeFormatter.ISO_DATE));
      return new BigObject20MB();
   }

   @Cacheable("contracts")
   public BigObject20MB getContractById(Long contractId, long requestStartTime) {
      log.debug("<{}> Fetch contract id={}", requestStartTime, contractId);
      return new BigObject20MB();
   }

   @Cacheable("invoices")
   public BigObject20MB getInvoiceByContractAndDate(InvoiceByDate param) {
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
}

/**
 * KEY POINTS
 * - [ideally] test that your caches work via automated tests (eg @SpringBootTest)
 */