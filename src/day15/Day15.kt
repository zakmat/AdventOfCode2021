package day15

import utils.*
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day15/test.txt"))
    solve(File("src/day15/input.txt"))
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val grid = file.readLines().map { line ->
        line.toList().map { it.digitToInt() }
    }

    val timeInMillis = measureTimeMillis {
        val stage1result = grid.calculateShortestPath(0 to 0, grid.gridSize - (1 to 1))
        println("Stage1: $stage1result")

        val stage2result = grid.enlarge(5).let { grid ->
            grid.calculateShortestPath(0 to 0, grid.gridSize - (1 to 1))
        }
        println("Stage2: $stage2result")

    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

private fun List<List<Int>>.enlarge(scale: Int): List<List<Int>> {
    return List(size * scale) { i ->
        List(this[0].size * scale) { j ->
            val repeatX = i / size
            val repeatY = j / this[0].size
            val offset = i % size to j % this[0].size
            (getValue(offset) + repeatX + repeatY).let { (it - 1) % 9 + 1 }
        }
    }
}


