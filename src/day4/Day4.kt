package day4

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day4/test.txt"))
    solve(File("src/day4/input.txt"))
}

fun List<List<Int>>.isWinning(numbers: List<Int>): Boolean {
    val rows = this
    val cols = indices.map { i -> indices.map { j -> this[j][i] } }
    return (rows + cols).any { numbers.toSet().containsAll(it) }
}

fun List<List<Int>>.score(numbers: List<Int>): Int {
    return this.flatten().toSet().subtract(numbers.toSet()).sum()
}

fun List<List<Int>>.finalScore(numbers: List<Int>): Int {
    return score(numbers) * numbers.last()
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val eol = System.lineSeparator()
    val text = file.readText().trim()
    val numbers = text.substringBefore("$eol$eol").split(',').map(String::toInt)
    val boards = text.substringAfter("$eol$eol").split("$eol$eol").map { group ->
        group.split(eol).map { line ->
            line.trim().split(' ').filter { it.isNotEmpty() }.map(String::toInt)
        }
    }

    val timeInMillis = measureTimeMillis {

        val winners = (5..numbers.size).asSequence().map {
            boards.firstNotNullOfOrNull { board ->
                if (board.isWinning(numbers.take(it)) and !board.isWinning(
                        numbers.take(it - 1)
                    )
                ) board.finalScore(numbers.take(it)) else null
            }
        }.filterNotNull()
        val stage1result = winners.first()
        println("Stage1: $stage1result")
        val stage2result = winners.last()
        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}
