package day7

import java.io.File
import java.lang.Math.abs
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day7/test.txt"))
    solve(File("src/day7/input.txt"))
}


fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val numbers = file.readText().trim().split(',').map(String::toInt).sorted()

    val timeInMillis = measureTimeMillis {
        val stage1result = numbers.calc(List<Int>::fuelStable)
        println("Stage1: $stage1result")
        val stage2result = numbers.calc(List<Int>::fuelIncreasing)
        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

fun List<Int>.fuelStable(align: Int): Int {
    return sumOf { abs(it - align) }
}

fun List<Int>.fuelIncreasing(align: Int): Int {
    return sumOf {
        val distance = abs(it - align)
        (1 + distance) * distance / 2
    }
}

fun List<Int>.calc(fuel: List<Int>.(Int) -> Int): Int {
    var (minIndex, minimal) = mapIndexed { index, align -> index to fuel(align) }.minByOrNull { it.second }!!
    var step = 1
    while (fuel(this[minIndex] + step - 1) > fuel(this[minIndex] + step)) {
        minimal = fuel(this[minIndex] + step)
        step++
    }
    step = 1
    while (fuel(this[minIndex] - step) < fuel(this[minIndex] - step + 1)) {
        minimal = fuel(this[minIndex] - step)
        step++
    }
    return minimal
}
