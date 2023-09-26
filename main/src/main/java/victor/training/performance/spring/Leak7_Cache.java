package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;
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
      String currentUsername = RandomStringUtils.random(8); // some random username
      // TODO CR: pass username as 2nd param below
      BigObject20MB data = cacheService.getContractById(1L , currentUsername);
      return "Contract id:1 = " + data + ", " + PerformanceUtil.getUsedHeap();
   }

   @GetMapping("customKey")
   public String customKey() {
      InvoiceByDate paramObj = new InvoiceByDate(13L, 2023, 10);
      System.out.println(paramObj.equals(paramObj));
      System.out.println(paramObj==paramObj);
      BigObject20MB data = cacheService.getInvoiceByContractAndDate(paramObj);
      return "Invoice = " + data + ", " + PerformanceUtil.getUsedHeap();
   }
}

@Service
@Slf4j
class CacheService {
   @Cacheable("invoices") // based on the param saves the return in a cache
   // (in-mem, or another one that you plug in)
   // the InvoiceByDate does not hashcode/equals
   public BigObject20MB getInvoiceByContractAndDate(InvoiceByDate param) {
      log.debug("Fetch invoice for {}", param);
      return new BigObject20MB();
   }
   // @Cacheable makes a proxy intercept the method call and return the cached value for that parameter (if any)

   @Cacheable("day-cache")
   public BigObject20MB getCachedDataForDay(LocalDateTime date) {
      log.debug("Fetch data for date: {}", date.format(DateTimeFormatter.ISO_DATE));
      return new BigObject20MB();
   }

   @Cacheable(value = "contracts", key = "#contractId") // fix
   public BigObject20MB getContractById(Long contractId, String currentUsername) { // FAIL: the cache key = Tuple<ContracId, String>
      log.debug("Fetch contract for id: {}", contractId);
      return new BigObject20MB();
   }

   // my pref:
   // @CacheEvict("bigcache") // delete from the cache the entry with that id.

//   @CachePut(value = "bigcache"/*, unless = "#result.someString != 'a'"*/) // what you return is going to be save in the "bigcache" under the key "id"
   // advantage: avoid a followup call to DB
//   public BigObject20MB updateBigObj(Long id) {
//      return repo.save(new BigObject20MB());
//   } // what you return is going to be save in the "bigcache" under the key "id"
}

// @Value or record java 17
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