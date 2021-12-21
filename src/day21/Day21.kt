package day21

import utils.Memoized
import utils.plus
import utils.times
import java.lang.Long.max
import kotlin.system.measureTimeMillis

data class Die(val max: Int, var current: Int)

data class State(
    val score: List<Int>,
    val pos: List<Int>,
    val roll: Int
)

fun Die.roll(count: Int): Int {
    var total = 0
    for (i in 0 until count) {
        total += ++current % max
    }
    return total
}

fun countDeterministic(start: Pair<Int, Int>): Int {
    val pos = mutableListOf(start.first, start.second)
    val scores = mutableListOf(0, 0)
    var current = 0
    val die = Die(1000, 0)
    while (scores.all { it < 1000 }) {
        val points = die.roll(3)
        pos[current] = (pos[current] + points) % 10
        scores[current] += pos[current] + 1
        current = (current + 1) % 2
    }
    return scores.minOf { it } * die.current
}

fun solve(dataSet: String, start: Pair<Int, Int>) {
    println("---- $dataSet ----")

    val timeInMillis = measureTimeMillis {
        val stage1result = countDeterministic(start)
        println("Stage1: $stage1result")

        val stage2resultCached = countWins(State(listOf(0, 0), listOf(start.first, start.second), 0)).let { max(it.first, it.second) }
        println("Stage2: $stage2resultCached")
    }
    println("---- $dataSet ---- Elapsed time: $timeInMillis")
}

val cachedCountWins = Memoized<State,Pair<Long,Long>> { countWins(it) }

private fun countWins(
    state: State,
): Pair<Long, Long> {
    if (state.score[0] >= 21) {
        return 1L to 0L
    } else if (state.score[1] >= 21) {
        return 0L to 1L
    }

    val current = state.roll % 2
    val occurrences = listOf(3 to 1, 4 to 3, 5 to 6, 6 to 7, 7 to 6, 8 to 3, 9 to 1)
    var result = 0L to 0L
    for ((pointSum, frequency) in occurrences) {
        val newPos = (state.pos[current] + pointSum) % 10
        val newScore = state.score[current] + newPos + 1
        result += cachedCountWins(
                state.copy(
                    pos = state.pos.mapIndexed { i, v -> if (i == current) newPos else v },
                    score = state.score.mapIndexed { i, v -> if (i == current) newScore else v },
                    roll = state.roll + 1
                )
        ) * frequency.toLong()
    }
    return result

}

fun main() {
    solve("test", 3 to 7)
    solve("input", 6 to 5)
}

