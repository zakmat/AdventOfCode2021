package day9

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day8/test.txt"))
    solve(File("src/day8/input.txt"))
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    file.readLines().map { line ->
        null
    }

    val timeInMillis = measureTimeMillis {
        val stage1result = null
        println("Stage1: $stage1result")
        val stage2result = null
        println("Stage2: $stage2result")
    }

    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}
