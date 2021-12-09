package day9

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day9/test.txt"))
    solve(File("src/day9/input.txt"))
}

fun List<List<Int>>.neighborHeights(x: Int, y: Int): List<Int> {
    val result = mutableListOf<Int>()
    if ((x > 0)) {
        result.add(this[x - 1][y])
    }
    if (y > 0) result.add(this[x][y - 1])
    if (y < this[0].size - 1) {
        result.add(this[x][y + 1])
    }
    if (x < this.size - 1) result.add(this[x + 1][y])

    return result
}

fun List<List<Int>>.higherPoints(lowPoint: Pair<Int, Int>): List<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()
    if ((lowPoint.first > 0)) {
        val candidate = lowPoint.first - 1 to lowPoint.second
        if (this[candidate.first][candidate.second] < 9 && this[candidate.first][candidate.second] > this[lowPoint.first][lowPoint.second])
            result.add(candidate)
    }
    if (lowPoint.second > 0) {
        val candidate = lowPoint.first to lowPoint.second - 1
        if (this[candidate.first][candidate.second] < 9 && this[candidate.first][candidate.second] > this[lowPoint.first][lowPoint.second])
            result.add(candidate)
    }
    if (lowPoint.second < this[0].size - 1) {
        val candidate = lowPoint.first to lowPoint.second + 1
        if (this[candidate.first][candidate.second] < 9 && this[candidate.first][candidate.second] > this[lowPoint.first][lowPoint.second])
            result.add(candidate)
    }
    if (lowPoint.first < this.size - 1) {
        val candidate = lowPoint.first + 1 to lowPoint.second
        if (this[candidate.first][candidate.second] < 9 && this[candidate.first][candidate.second] > this[lowPoint.first][lowPoint.second])
            result.add(candidate)
    }
    return result.toList()
}

fun List<List<Int>>.calcBasinSize(lowPoint: Pair<Int, Int>): Int {
    val toCheck = mutableSetOf(lowPoint)
    val checked = mutableSetOf<Pair<Int, Int>>()
    while (toCheck.isNotEmpty()) {
        val candidate = toCheck.first()
        toCheck.remove(candidate)
        checked.add(candidate)
        toCheck.addAll(higherPoints(candidate))
    }
    return checked.size
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val heights = file.readLines().map { line ->
        line.toList().map { it.digitToInt() }
    }

    val timeInMillis = measureTimeMillis {
        val lowPoints = sequence<Pair<Int, Int>> {
            heights.indices.forEach { i ->
                heights[0].indices.forEach { j ->
                    if (heights[i][j] < heights.neighborHeights(i, j).minOf { it }) {
                        yield(i to j)
                    }
                }
            }
        }.toList()

        val stage1result = lowPoints.sumOf { heights[it.first][it.second] + 1 }
        println("Stage1: $stage1result")

        val results = lowPoints.map { heights.calcBasinSize(it) }.sortedDescending()

        val stage2result = results.take(3).fold(1, Int::times)
        println("Stage2: $stage2result")
    }

    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}
