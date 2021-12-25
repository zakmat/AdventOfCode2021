package day25

import utils.lower
import utils.update
import utils.upper
import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.system.measureTimeMillis

var rowSize = 0
var colSize = 0
fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val cucumbers = file.readLines().flatMapIndexed { row, line ->
        rowSize = row
        line.toList().mapIndexedNotNull { col, cell ->
            if (row == 0) {
                colSize = col
            }
            when (cell) {
                '>', 'v' -> (row to col) to cell
                else -> null
            }
        }
    }.toMap()
    rowSize += 1
    colSize += 1

    val timeInMillis = measureTimeMillis {
        var step = 0
        var initial = cucumbers
        while(true) {
            val nextEastState = simulate(initial, step++)
            val nextState = simulate(nextEastState, step++)
            if (nextState == initial) {
                break
            }
            initial = nextState
        }
        val stage1result = step / 2
        println("Stage1:$stage1result")

    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

private fun Map<Pair<Int,Int>, Char>.print(rowSize: Int, colSize: Int) {
    for (r in 0 until rowSize) {
        for (c in 0 until colSize) {
            print(this.withDefault { '.' }.getValue(r to c))
        }
        println()
    }
    println()
}

fun next(pos: Pair<Int, Int>, right: Boolean): Pair<Int, Int> {
    if (right) {
        return pos.first to (pos.second + 1) % colSize
    }
    return ((pos.first + 1) % rowSize) to pos.second
}

fun simulate(cucumbers: Map<Pair<Int, Int>, Char>, step: Int): Map<Pair<Int, Int>, Char> {
    val right = step % 2 == 0
    return cucumbers.map { (pos, cell) ->
        if ((right && cell == 'v') || (!right && cell == '>')) {
            pos to cell
        } else {
            val next = next(pos, right)
            if (cucumbers.containsKey(next)) {
                pos to cell
            } else {
                next to cell
            }
        }
    }.toMap()
}

//1214313344725528
fun main() {
    solve(File("src/day25/test.txt"))
    solve(File("src/day25/input.txt"))
}

