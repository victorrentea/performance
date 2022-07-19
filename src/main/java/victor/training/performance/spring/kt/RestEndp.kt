package victor.training.performance.spring.kt

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.persistence.EntityManager

@RestController
class RestEndp(
//    val service: MyService,
) {
    @GetMapping("kt")
    fun hello() = "Hi"
}