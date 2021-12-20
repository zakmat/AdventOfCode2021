package day20

import utils.gridMapIndexed
import utils.x
import utils.y
import java.io.File
import kotlin.system.measureTimeMillis

fun List<List<Int>>.adjAsBinary(
    x: Int,
    y: Int,
    default: Int
): Int {
    val steps = listOf(
        -1 to -1, -1 to 0, -1 to 1,
        0 to -1, 0 to 0, 0 to 1,
        1 to -1, 1 to 0, 1 to 1,
    )
    return steps.map { step -> x + step.first to y + step.second }
        .map { (x, y) ->
            if (x >= 0 && y >= 0 && x < size && y < this[0].size) {
                this[x][y]
            } else {
                default // default
            }
        }.joinToString("").toInt(2)
}

fun List<List<Int>>.recalculate(algo: List<Int>, default: Int): List<List<Int>> {
    val recalculate = this.gridMapIndexed { point, _ ->
        algo[this.adjAsBinary(point.x, point.y, default)]
    }

    val leftLine = List(this.size) {
        algo[this.adjAsBinary(it, -1, default)]
    }
    val rightLine = List(this.size) {
        algo[this.adjAsBinary(it, this[0].size, default)]
    }
    val upLine = listOf(algo[this.adjAsBinary(-1, -1, default)]) +
            List(this[0].size) { y ->
                algo[this.adjAsBinary(-1, y, default)]
            } + listOf(algo[this.adjAsBinary(-1, this[0].size, default)])
    val downLine = listOf(algo[this.adjAsBinary(size, -1, default)]) +
            List(this[0].size) { y ->
                algo[this.adjAsBinary(this[0].size, y, default)]
            } + listOf(algo[this.adjAsBinary(this.size, this[0].size, default)])

    return listOf(upLine) + recalculate.mapIndexed { i, line ->
        listOf(leftLine[i]) + line + listOf(rightLine[i])
    } + listOf(downLine)
}

fun List<List<Int>>.countAfterEnhance(algo: List<Int>, steps: Int): Int {
    var newImage = this
    for (i in 0 until steps) {
        newImage = newImage.recalculate(algo, if (algo[0] == 0) 0 else i % 2)
    }
    return newImage.flatten().count { it == 1 }
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val eol = System.lineSeparator()
    val algo =
        file.readText().trim().substringBefore("$eol$eol").replace('#', '1').replace('.', '0')
            .map { it.digitToInt(2) }
    val image = file.readText().trim().substringAfter("$eol$eol").split(eol).map { line ->
        line.replace('#', '1').replace('.', '0').toList().map { it.digitToInt(2) }
    }

    val timeInMillis = measureTimeMillis {
        val stage1result = image.countAfterEnhance(algo, 2)
        println("Stage1: $stage1result")

        val stage2result = image.countAfterEnhance(algo, 50)
        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

fun main() {
    solve(File("src/day20/test.txt"))
    solve(File("src/day20/input.txt"))
}

