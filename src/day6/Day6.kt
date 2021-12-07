package day6

import utils.toward
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day6/test.txt"))
    solve(File("src/day6/input.txt"))
}


fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val numbers = file.readText().trim().split(',').map(String::toInt)

    val counts = (0..8).associateWith { day ->
        numbers.count { it == day }.toLong()
    }

    val timeInMillis = measureTimeMillis {
        val stage1result = simulate(counts, 80).values.sum()
        println("Stage1: $stage1result")
        val stage2result = simulate(counts, 256).values.sum()
        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

fun simulate(numbers: Map<Int, Long>, cycles: Int): Map<Int, Long> {
    var currentCycle = numbers
    for (i in 0 until cycles) {
        currentCycle = evolve(currentCycle)
    }
    return currentCycle
}

fun evolve(numbers: Map<Int, Long>) : Map<Int, Long> {
    return (0..8).associateWith { day ->
        when (day) {
            6 -> (numbers[7]?:0) + (numbers[0]?:0)
            8 -> numbers[0]?:0
            else -> numbers[day + 1]?:0
        }
    }
}