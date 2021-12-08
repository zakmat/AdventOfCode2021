package day8

import utils.permutations
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day8/test.txt"))
    solve(File("src/day8/input.txt"))
}

val initialMapping = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg","abcdfg").map { it.toSet()}

fun fit(perm: String, patterns: List<String>, outputs: List<String>): Int? {
    val currentMap = initialMapping.map { it.mutate(perm) }.toSet()
    return if (patterns.map {it.toSet()}.toSet() == currentMap)  {
        outputs.map { currentMap.indexOf(it.toSet()) }.joinToString("").toInt()
    } else null
}

fun Set<Char>.mutate(perm: String): Set<Char> {
    return map { perm["abcdefg".indexOf(it)] }.toSet()
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
        val stage2result = segments.map { (p, o) ->
            permutations.firstNotNullOf { permutation ->
                fit(permutation, p, o)
            }
        }.sum()
        println("Stage2: $stage2result")
    }

    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}
