package victor.training.performance.spring

import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import victor.training.performance.util.BigObject20MB
import victor.training.performance.util.PerformanceUtil
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("leak7")
class Leak7_Cache {
    @Autowired
    private val stuff: Stuff? = null
    @GetMapping
    fun test(): String {
        val currentUsername = RandomStringUtils.random(8)
        // TODO pass this as param
        val data = stuff!!.returnCachedDataForDay(LocalDate.now())
        return "Tools won't always shield you from mistakes: data=" + data + ", " + PerformanceUtil.getUsedHeap()
    }
}

@Service
@Slf4j
open class Stuff {
    @Cacheable("missed-cache") // = a proxy intercepts the method call and returns the cached value for that parameter
    open fun returnCachedDataForDay(date: LocalDate): BigObject20MB {
        println("Fetch data for date: " + date.format(DateTimeFormatter.ISO_DATE))
        return BigObject20MB()
    }
}