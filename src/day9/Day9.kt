package day9

import utils.*
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day9/test.txt"))
    solve(File("src/day9/input.txt"))
}

fun List<List<Int>>.neighborHeights(x: Int, y: Int): List<Int> {
    return adjacent(x, y).map { this[it.x][it.y] }
}

fun List<List<Int>>.higherPoints(lowPoint: Pair<Int, Int>): List<Pair<Int, Int>> {
    return adjacent(
        lowPoint.x,
        lowPoint.y
    ).filter { (x, y) -> this[x][y] < 9 && this[x][y] > this[lowPoint.x][lowPoint.y] }
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
        val lowPoints = heights.gridMapIndexed { i, j, height ->
            if (height < heights.neighborHeights(i, j).minOf { it }) {
                i to j
            } else null
        }.flatten().filterNotNull()

        val stage1result = lowPoints.sumOf { heights[it.x][it.y] + 1 }
        println("Stage1: $stage1result")

        val stage2result = lowPoints.map { heights.calcBasinSize(it) }.sortedDescending().take(3)
            .fold(1, Int::times)
        println("Stage2: $stage2result")
    }

    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}
