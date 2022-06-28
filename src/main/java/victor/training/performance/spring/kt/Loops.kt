package victor.training.performance.spring.kt

fun main(args: Array<String>) {
    println("classic")
    for(i in 1..10) println(i)

    println("until")
    for(i in 1 until 10) println(i)

    println("repeat")
    repeat(10) {
        println(it)
    }

    println("forEach")
    (0..10).forEach {
        println(it)
    }

    println("classic + step")
    for(i in 1..10 step 2) println(i)


}