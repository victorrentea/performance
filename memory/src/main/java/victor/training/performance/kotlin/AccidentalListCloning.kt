//package victor.training.performance.kotlin
//
//
//fun main() {
//    val list5K = (1..5000).toList()
//
//    // allocates 100 MB
//    val immutableList = list5K.fold(listOf<Int>()) { prev, e -> prev + e } // clones list every iteration
//    println(System.identityHashCode(immutableList))
//
//    // allocates 0 MB
//    val mutableList = list5K.fold(mutableListOf<Int>()) { prev, e ->
//        prev.add(e);
//        prev
//    }
//    println(System.identityHashCode(mutableList))
//
//    data class Result(
//        val result: List<Int> = listOf(),
//        val sum: Int = 0,
//        val startTime: Long = System.currentTimeMillis()
//    )
//    // allocates 100 MB
//    val copy = list5K.fold(Result()) { prev, e -> prev.copy(result = prev.result + e, sum = prev.sum + e) }
//    println(System.identityHashCode(copy.result))
//
//    // allocates 0 MB
//    data class MaxMin(val max: Int = Int.MIN_VALUE, val min: Int = Int.MAX_VALUE)
//    val maxMin = list5K.fold(MaxMin()) { prev, e -> MaxMin(max = maxOf(prev.max, e), min = minOf(prev.min, e)) }
//    println(System.identityHashCode(maxMin))
//}
//
//
