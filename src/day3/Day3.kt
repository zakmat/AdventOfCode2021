package day3

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day3/test.txt"))
    solve(File("src/day3/input.txt"))
}

fun List<List<Int>>.bitCount() : List<Int> {
    val bitCount = this.drop(1).fold(first()) { acc, bitLine ->
        acc.zip(bitLine).map { it.first + it.second }
    }
    return bitCount
}

fun List<List<Int>>.mostCommon() : List<Int> {
    val bitCount = bitCount()

    return bitCount.map { count -> if(size <= count*2) 1 else 0 }
}

fun List<List<Int>>.leastCommon() : List<Int> {
    return mostCommon().map { bit -> if (bit == 1) 0 else 1}
}

fun List<List<Int>>.filterNumbers(applyCriteria : (List<List<Int>>) -> List<Int>) : List<Int> {
    var filtered = this
    var currentBit = 0
    do {
        val criteria = applyCriteria(filtered)
        filtered = filtered.filter { it[currentBit] == criteria[currentBit]}
        currentBit += 1
    } while(filtered.size > 1)
    return filtered.first()
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val bits = file.readLines().map { line ->
        line.toList().map { if (it == '1') 1 else 0}
    }
    val timeInMillis = measureTimeMillis {
        val rate = bits.mostCommon().joinToString("")
        val epsilon = bits.leastCommon().joinToString ("")

        val stage1result = rate.toInt(2) * epsilon.toInt(2)
        println("Stage1: $stage1result")

        val oxyRating = bits.filterNumbers { it.mostCommon() }.joinToString("")
        val scubaRating = bits.filterNumbers { it.leastCommon() }.joinToString ("")

        val stage2result = oxyRating.toInt(2) * scubaRating.toInt(2)
        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}
