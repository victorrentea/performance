package victor.training.performance.spring.kt

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun heavyIO() {
    println("Call network")
    java.lang.RuntimeException("Before").printStackTrace()
    delay(1000)
    java.lang.RuntimeException("After").printStackTrace() // different call stack
    println("DONE network")
}

fun main(args: Array<String>) {
    GlobalScope.launch {
        println("Start")
        heavyIO()
        heavyIO()
        println("Done")
        java.lang.RuntimeException().printStackTrace()
    }
    Thread.sleep(3000)
}
