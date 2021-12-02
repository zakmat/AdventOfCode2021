package day1

import java.io.File
import kotlin.system.measureTimeMillis


fun main() {
    solve(File("src/day1/test.txt"))
    solve(File("src/day1/input.txt"))
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val numbers = file.readLines().map { line ->
       line.toInt()
    }
    val timeInMillis = measureTimeMillis {
        val stage1result = numbers.zipWithNext().count { it.first < it.second}

        println("Stage1: $stage1result" )

        val stage2result = numbers.zip(numbers.drop(1)).zip(numbers.drop(2)).map { (pair, third) ->
            pair.first + pair.second + third
        }.zipWithNext().count { it.first < it.second }


        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

