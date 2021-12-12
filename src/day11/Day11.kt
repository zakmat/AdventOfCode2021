package day11

import utils.*
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day11/test.txt"))
    solve(File("src/day11/input.txt"))
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val grid = file.readLines().map { line ->
        line.toList().map { it.digitToInt() }
    }

    val timeInMillis = measureTimeMillis {
        val stage1result = simulate(grid, 100).first
        println("Stage1: $stage1result")

        val stage2result = simulate(grid, 1000).second
        println("Stage2: $stage2result")
    }

    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

fun simulate(input: List<List<Int>>, steps: Int): Pair<Int, Int?> {
    var grid = input
    var flashes = 0
    var step = 0
    while (step++ < steps) {
        val flashed = mutableSetOf<Pair<Int, Int>>()
        val toFlash = mutableSetOf<Pair<Int, Int>>()
        // initial increase
        grid = grid.gridMap { item -> item + 1 }
        // flashes calculation
        grid.gridMapIndexed { pos, item -> if (item >= 10) toFlash.add(pos) }
        while (toFlash.isNotEmpty()) {
            val candidate = toFlash.first()
            toFlash.remove(candidate)
            flashed.add(candidate)
            val neighbours = grid.adjacent(candidate, true, true)
            grid = grid.gridMapIndexed { pos, item -> if (pos in neighbours) item + 1 else item }
            grid.gridMapIndexed { pos, item -> if (pos !in flashed && item >= 10) toFlash.add(pos) }
        }
        // reset
        grid = grid.gridMapIndexed { point, item -> if (point in flashed) 0 else item }

        flashes += flashed.size

        if (flashed.size == grid.size * grid[0].size) {
            return flashes to step
        }
    }
    return flashes to null
}