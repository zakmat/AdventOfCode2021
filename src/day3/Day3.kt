package day3

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day3/test.txt"))
    solve(File("src/day3/input.txt"))
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val lines = file.readLines().map { line ->
        line
    }
    val timeInMillis = measureTimeMillis {
        val stage1result = lines.first()

        println("Stage1: $stage1result")

        val stage2result = lines.first()

        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}
