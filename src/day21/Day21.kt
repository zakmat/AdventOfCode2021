package day21

import java.io.File
import java.lang.Long.max
import kotlin.system.measureTimeMillis

data class Die(val min: Int, val max: Int, var current: Int)

data class State(
    val fstScore: Int,
    val sndScore: Int,
    val fstPos: Int,
    val sndPos: Int,
    val roll: Int
)

fun Die.roll(count: Int): Int {
    var total = 0
    for (i in 0 until count) {
        total += current++
        if (current > max) {
            current = min
        }
    }
    return total
}

fun countDeterministic(start: Pair<Int, Int>): Int {
    val pos = mutableListOf(start.first, start.second)
    val scores = mutableListOf(0, 0)
    var dieCounter = 0
    var currentPlayer = 0
    val die = Die(1, 1000, 1)
    while (scores.all { it < 1000 }) {
        val points = die.roll(3)
        pos[currentPlayer] += points
        pos[currentPlayer] %= 10
        scores[currentPlayer] += pos[currentPlayer] + 1
        currentPlayer += 1
        currentPlayer %= 2
    }
    return scores.minOf { it } * (die.current - 1)
}

fun solve(dataSet: String, start: Pair<Int, Int>) {
    println("---- $dataSet ----")

    val timeInMillis = measureTimeMillis {
        val stage1result = countDeterministic(start)
        println("Stage1: $stage1result")

        val universes = generateUniverses(start, 21)
        val stage2result = universes.countForResult(21)
        println("Stage2: $stage2result")
    }
    println("---- $dataSet ---- Elapsed time: $timeInMillis")
}

private fun generateUniverses(start: Pair<Int, Int>, winningScore: Int): MutableMap<State, Long> {
    val universes =
        mutableMapOf(State(0, 0, start.first, start.second, -1) to 1L).withDefault { 0L }
    for (roll in 0..41) {
        for (fstScore in 0..30) {
            for (sndScore in 0..30) {
                for (fstPos in 0..9) {
                    for (sndPos in 0..9) {
                        val current = State(fstScore, sndScore, fstPos, sndPos, roll)
                        val player = roll % 2
                        if (player == 0) {
                            for (i in 1..3) {
                                for (j in 1..3) {
                                    for (k in 1..3) {
                                        val previous = current.copy(
                                            fstPos = fstPos.prevPos(i + j + k),
                                            fstScore = fstScore - fstPos - 1,
                                            roll = roll - 1
                                        )
                                        if (previous.isWinning(winningScore))
                                            continue
                                        val previousCount = universes.getValue(previous)
                                        if (previousCount > 0)
                                            universes[current] =
                                                universes.getValue(current) + previousCount
                                    }
                                }
                            }
                        } else {
                            for (i in 1..3) {
                                for (j in 1..3) {
                                    for (k in 1..3) {
                                        val previous = current.copy(
                                            sndPos = sndPos.prevPos(i + j + k),
                                            sndScore = sndScore - sndPos - 1,
                                            roll = roll - 1
                                        )
                                        if (previous.isWinning(winningScore))
                                            continue
                                        val previousCount = universes.getValue(previous)
                                        if (previousCount > 0)
                                            universes[current] =
                                                universes.getValue(current) + previousCount
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    return universes
}

private fun State.isWinning(winning: Int): Boolean {
    return fstScore >= winning || sndScore >= winning
}

private fun MutableMap<State, Long>.countForResult(i: Int): Long {
    val fstResult =
        this.filterKeys { it.fstScore >= i && it.sndScore < i && it.roll % 2 == 0 }.values.sum()
    val sndResult =
        this.filterKeys { it.sndScore >= i && it.fstScore < i && it.roll % 2 == 1 }.values.sum()
    return max(fstResult, sndResult)
}

fun Int.prevPos(move: Int): Int {
    return (this + 10 - move) % 10
}

fun main() {
    solve("test", 3 to 7)
    solve("input", 6 to 5)
}

