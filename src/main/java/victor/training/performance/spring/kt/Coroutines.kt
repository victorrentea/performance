package victor.training.performance.spring.kt

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun myFlow() {
    heavyIO()
}

private suspend fun heavyIO() {
    println("Call network")
    delay(1000)
    println("DONE network")
}

fun main(args: Array<String>) {
    GlobalScope.launch {
        println("Start")
        myFlow()
        myFlow()
        println("Done")
        java.lang.RuntimeException().printStackTrace()
    }
    Thread.sleep(3000)
}
