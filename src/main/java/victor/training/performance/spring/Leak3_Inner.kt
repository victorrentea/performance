package victor.training.performance.spring

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import victor.training.performance.spring.CachingMethodObject.UserRightsCalculator
import victor.training.performance.util.BigObject20MB
import victor.training.performance.util.PerformanceUtil
import java.util.function.Supplier

@RestController
@RequestMapping("leak3")
class Leak3_Inner {
    @GetMapping
    fun home(): String {
        return "Do you know Java? <br>If you think you do:<br>" +
                "<li>Start here: <a href='/leak3/puzzle'>puzzle</a> (pauses 20 sec to get a heap dump)" +
                "<li><a href='/leak3/anon'>anon</a>" +
                "<li><a href='/leak3/map'>map</a> "
    }

    @GetMapping("puzzle")
    fun puzzle(): String {
        val calculator = CachingMethodObject().createRightsCalculator()
        bizLogicUsingCalculator(calculator)
        return "Done"
    }

    //<editor-fold desc="Entry points of more similar leaks">
    @GetMapping("anon")
    fun anon(): String {
        val supplier = CachingMethodObject().anonymousVsLambdas()
        PerformanceUtil.sleepq(20000) // some long workflow
        return supplier()
    }

    //</editor-fold>
    private fun bizLogicUsingCalculator(calculator: UserRightsCalculator) {
        if (!calculator.hasRight("launch")) {
            return
        }
        PerformanceUtil.sleepq(20000) // long flow and/or heavy parallel load
    }
}

internal class CachingMethodObject {
    class UserRightsCalculator {
        // an instance of this is kept on current thread
        fun hasRight(task: String?): Boolean {
            println("Stupid Code")
            //			System.out.println(bigMac);
            // what's the connection between this instance and the 'bigMac' field ?
            return true
        }
    }

    private val bigMac = BigObject20MB()
    fun createRightsCalculator(): UserRightsCalculator {
        return UserRightsCalculator()
    }

    // then, some more (amazing) leaks .....
    //<editor-fold desc="Lambdas vs Anonymous implementation">
    fun anonymousVsLambdas(): () -> String{
//		return () -> "Happy";
        return { "Happy $bigMac" }
    } //</editor-fold>
}