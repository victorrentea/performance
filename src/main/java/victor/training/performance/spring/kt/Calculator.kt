package victor.training.performance.spring.kt

typealias Listener = (Int) -> Unit
class Calculator {
    var listeners: MutableSet<Listener> = HashSet()

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun sum(a: Int, b: Int): Int {
        val s = a + b
        for (listener in listeners) {
            listener(s)
        }
        return s
    }
}