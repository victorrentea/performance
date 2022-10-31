//package victor.training.performance.spring.kt
//
//fun main() {
//    val c = Calculator()
//    // TODO fix: explicitly type the Lambda (Calculator.Listener)
//    val listener = { sum: Int ->
//        println("Listener got: $sum")
//    }
//
//    c.addListener(listener)
//    c.sum(10, 20)
//    c.removeListener(listener)
//    println("Nothing should print after this")
//    c.sum(10, 20)
//}