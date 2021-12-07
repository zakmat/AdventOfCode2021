package day5

import utils.toward
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day5/test.txt"))
    solve(File("src/day5/input.txt"))
}

data class Segment(val startx: Int, val starty: Int, val endx: Int, val endy: Int)

fun Segment.calculatePoints(withDiagonals: Boolean): List<Pair<Int, Int>> {
    if (startx == endx) {
        return (starty toward endy).map { startx to it }
    }
    if (starty == endy) {
        return (startx toward endx).map { it to starty }
    }

    if (!withDiagonals) {
        return emptyList()
    }

    return (startx toward endx).zip(starty toward endy)
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val segments = file.readLines().map { line ->
        val start = line.substringBefore(" -> ").split(',').map(String::toInt).let { it.first() to it.last() }
        val end = line.substringAfter(" -> ").split(',').map(String::toInt).let { it.first() to it.last() }
        Segment(start.first, start.second, end.first, end.second)
    }

    val timeInMillis = measureTimeMillis {
        val stage1result = countOverlaps(segments, false)
        println("Stage1: $stage1result")
        val stage2result = countOverlaps(segments, true)
        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

fun countOverlaps(segments: List<Segment>, withDiagonals: Boolean): Int {
    val points = mutableMapOf<Pair<Int, Int>, Int>().withDefault { 0 }
    segments.forEach { segment ->
        segment.calculatePoints(withDiagonals).forEach { point ->
            points[point] = 1 + points.getValue(point)
        }
    }
    return points.values.count { it > 1 }
}