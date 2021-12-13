package day13

import utils.x
import utils.y
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day13/test.txt"))
    solve(File("src/day13/input.txt"))
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val eol = System.lineSeparator()
    val dots =
        file.readText().trim().substringBefore("$eol$eol").split(eol)
            .map { line ->
                line.split(",").map(String::toInt).let { it.first() to it.last() }
            }

    val folds =
        file.readText().trim().substringAfter("$eol$eol").split(eol)
            .map { line ->
                line.substringAfterLast(" ").split("=").let { it.first() to it.last().toInt() }
            }

    val timeInMillis = measureTimeMillis {
        val stage1result = dots.fold(folds.take(1)).toSet().size
        println("Stage1: $stage1result")

        val result = dots.fold(folds).toSet()
        val maxx = result.maxOf { it.x }
        val maxy = result.maxOf { it.y }
        for (i in 0..maxy) {
            for (j in 0..maxx) {
                print(if ((j to i) in result) '#' else ' ')
            }
            println()
        }
    }

    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

fun List<Pair<Int, Int>>.fold(folds: List<Pair<String, Int>>): List<Pair<Int, Int>> {
    val result = folds.fold(this) { result, (direction, index) -> // LOL
        when (direction) {
            "x" -> result.map { dot -> (if (dot.x > index) index - (dot.x - index) else dot.x) to dot.y }
            "y" -> result.map { dot -> dot.x to if (dot.y > index) index - (dot.y - index) else dot.y }
            else -> error("Invalid")
        }
    }
    return result
}
