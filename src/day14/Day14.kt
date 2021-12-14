package day14

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day14/test.txt"))
    solve(File("src/day14/input.txt"))
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val eol = System.lineSeparator()
    val template =
        file.readText().trim().substringBefore("$eol$eol")

    val insertions =
        file.readText().trim().substringAfter("$eol$eol").split(eol)
            .map { line ->
                line.split(" -> ").let { it.first() to it.last() }
            }.toMap().withDefault { "" }
    println(template)
    println(insertions)

    val timeInMillis = measureTimeMillis {
        val stage1result = template.simulate(insertions, 10)
        println("Stage1: $stage1result")

        val stage2result = template.simulate2(insertions, 40)
        println("Stage2: $stage2result")

        println()

    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

fun String.simulate(insertions: Map<String, String>, steps: Int): Int {
    val result = (0 until steps).fold(this) { accu, _ ->
        accu.windowed(2).map { polymer ->
            if (insertions.containsKey(polymer))
                polymer.first() + insertions.getValue(polymer)
            else
                polymer.first()
        }.joinToString("") + last()
    }

    return result.groupingBy { it }.eachCount().map { it.value }.sorted()
        .let { it.last() - it.first() }
}

fun String.simulate2(insertions: Map<String, String>, steps: Int): Long {
    var step = 0
    var polyCounts = this.windowed(2).groupingBy { it }.eachCount().mapValues { it.value.toLong() }
    while (step++ < steps) {
        val after = mutableMapOf<String, Long>().withDefault { 0L }

        polyCounts.forEach { (poly, count) ->
            if (insertions.containsKey(poly)) {
                val one = poly.first() + insertions.getValue(poly)
                val two = insertions.getValue(poly) + poly.last()
                after[one] = after.getValue(one) + count
                after[two] = after.getValue(two) + count
            } else {
                after[poly] = after.getValue(poly) + count
            }
        }
        polyCounts = after

    }

    val letterCounts = mutableMapOf<Char, Long>().withDefault { 0L }
    polyCounts.forEach { (poly, count) ->
        letterCounts[poly.first()] = letterCounts.getValue(poly.first()) + count
        letterCounts[poly.last()] = letterCounts.getValue(poly.last()) + count
    }
    letterCounts[first()] = letterCounts.getValue(first()) + 1
    letterCounts[last()] = letterCounts.getValue(last()) + 1
    return letterCounts.map { it.value }.sorted().let { it.last() - it.first() } / 2
}
