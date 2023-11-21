package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;
import victor.training.performance.util.PerformanceUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
      String currentUsername = RandomStringUtils.random(8); // some random username
      // TODO CR: pass username as 2nd param below
      BigObject20MB data = cacheService.getContractById(1L , currentUsername);
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
   // @Cacheable makes a proxy intercept the method call and return the previously cached value for that parameter (if any)
   @Cacheable("day-cache")
   public BigObject20MB getCachedDataForDay(LocalDate date) {
      log.debug("Fetch data for date: {}", date.format(DateTimeFormatter.ISO_DATE));
      return new BigObject20MB();
   }

   @Cacheable("contracts")
   public BigObject20MB getContractById(Long contractId, String currentUsername) { // cache hit = 0 pt ca currentUsername niciodata nu  e la fel
      log.debug("Fetch contract for id: {}", contractId);
      return new BigObject20MB();
   }

   @Cacheable("invoices")
   public BigObject20MB getInvoiceByContractAndDate(InvoiceByDate param) {
      log.debug("Fetch invoice for {}", param);
      return new BigObject20MB();
   }
}

record InvoiceByDate(Long contractId, int year, int month) { // iti da si hashCode/equals
}

/**
 * KEY POINTS
 * - [ideally] test that your caches work via automated tests (eg @SpringBootTest)
 */