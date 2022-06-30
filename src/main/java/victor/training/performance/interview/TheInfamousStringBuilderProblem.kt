package victor.training.performance.interview

import victor.training.performance.util.PerformanceUtil
import java.io.FileWriter
import java.io.IOException
import java.sql.PreparedStatement
import java.sql.Statement
import java.util.stream.Collectors
import java.util.stream.IntStream

object TheInfamousStringBuilderProblem {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val elements = IntStream.range(1, 50_000_00) // try 50K x 6 chars : see TLAB
            .mapToObj { n: Int -> "hahaha" }
            .collect(Collectors.toList())
        PerformanceUtil.waitForEnter()
        println("Start writing contents!")
        val t0 = System.currentTimeMillis()

        FileWriter("out.txt").use {
            toStudy(elements, it)
        }
//val s: PreparedStatement
//s.set
        println("Done. Took " + (System.currentTimeMillis() - t0))
        PerformanceUtil.waitForEnter()
    }

    private fun toStudy(elements: MutableList<String>, fileWriter: FileWriter) {
//        var s: String? = ""
//        for (element in elements) {
//            s += element
//        }
        elements.forEach{ fileWriter.write(it) }
    }
}