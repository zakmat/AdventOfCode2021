package day8

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day8/test.txt"))
    solve(File("src/day8/input.txt"))
}

fun String.permutations(): List<String> {
    if (length == 1)
        return listOf(this)
    return (this.indices).map { i ->
        removeRange(i, i + 1).permutations().map { it + this[i] }
    }.flatten()
}

val initialMapping = mapOf(
    "abcefg".toSet() to 0,
    "cf".toSet() to 1,
    "acdeg".toSet() to 2,
    "acdfg".toSet() to 3,
    "bcdf".toSet() to 4,
    "abdfg".toSet() to 5,
    "abdefg".toSet() to 6,
    "acf".toSet() to 7,
    "abcdefg".toSet() to 8,
    "abcdfg".toSet() to 9
)

fun fit(perm: String, patterns: List<String>, outputs: List<String>): Int? {
    val currentMap = initialMapping.mutate(perm)
    if (outputs.all { output -> currentMap.keys.contains(output.toSet()) } &&
        patterns.all { output -> currentMap.keys.contains(output.toSet()) }
    ) {
        val result = outputs.map { currentMap[it.toSet()] }.joinToString("").toInt()
        return result
    }
    return null
}

fun Set<Char>.mutate(perm: String): Set<Char> {
    return map {
        when (it) {
            'a' -> perm[0]
            'b' -> perm[1]
            'c' -> perm[2]
            'd' -> perm[3]
            'e' -> perm[4]
            'f' -> perm[5]
            'g' -> perm[6]
            else -> error("Invalid char")
        }
    }.toSet()
}

fun Map<Set<Char>, Int>.mutate(perm: String): Map<Set<Char>, Int> {
    return map { (k, v) -> k.mutate(perm) to v }.toMap()
}


fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val segments = file.readLines().map { line ->
        line.substringBefore("|").trim().split(' ') to
                line.substringAfter("|").trim().split(' ')
    }

    val timeInMillis = measureTimeMillis {
        val stage1result = segments.map { it.second }.flatten().map { it.length }.count { num -> setOf(7, 4, 2, 3).contains(num) }
        println("Stage1: $stage1result")
        val permutations = "abcdefg".permutations().asSequence()
        val result = segments.map { (p, o) ->
            permutations.firstNotNullOf { mapping ->
                fit(mapping, p, o)
            }
        }.sum()
        val stage2result = result
        println("Stage2: $stage2result")
    }

    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}
