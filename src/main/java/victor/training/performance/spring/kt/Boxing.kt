package victor.training.performance.spring.kt

import victor.training.performance.util.PerformanceUtil

object Boxing {
    var i1 = 2
    var i2: Int = 2
    var i3: Int? = 2

}
fun main() {
    PerformanceUtil.printJfrFile()
    for (i in 0..1000000) {
        Boxing.i3 = i
    }
    println("Done")
}
