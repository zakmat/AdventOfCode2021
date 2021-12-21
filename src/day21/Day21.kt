package day21

import utils.*
import java.lang.Long.max
import java.util.*
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
        val initial = State(
            listOf(0, 0),
            listOf(start.first, start.second),
            0
        )

        val stage2resultCached = countWinsCached(
            initial
        ).let { max(it.first, it.second) }
        println("Stage2: $stage2resultCached")


        val stage2resultDp = countWinsDp(initial)
        println("Stage2: $stage2resultDp")
    }
    println("---- $dataSet ---- Elapsed time: $timeInMillis")
}

private fun countWinsDp(initial: State): Long {
    val comparator: Comparator<State> = compareBy { it.score.sum() }
    val queue = PriorityQueue(comparator)
    queue.add(initial)
    val marked = hashSetOf(initial)

    val dp = hashMapOf(initial to 1L).withDefault { 0L }
    val occurrences = listOf(3 to 1, 4 to 3, 5 to 6, 6 to 7, 7 to 6, 8 to 3, 9 to 1)
    while (queue.isNotEmpty()) {
        val state = queue.peek()
        queue.remove()
        for ((move, frequency) in occurrences) {
            val next = state.next(move)

            if (!next.isWinning() && !marked.contains(next)) {
                queue.add(next)
                marked.add(next)
            }
            dp[next] = dp.getValue(next) + dp.getValue(state) * frequency
        }
    }

    return dp.countForResult(21)
}

private fun State.next(move: Int): State {
    val newPos = (pos[roll % 2] + move) % 10
    val newScore = score[roll % 2] + newPos + 1
    return copy(
        pos = pos.mapIndexed { i, v -> if (i == (roll % 2)) newPos else v },
        score = score.mapIndexed { i, v -> if (i == (roll % 2)) newScore else v },
        roll = roll + 1
    )
}

private fun State.isWinning(): Boolean = score.any { it >= 21 }

private fun MutableMap<State, Long>.countForResult(i: Int): Long {
    val fstResult =
        this.filterKeys { it.score[0] >= i && it.score[1] < i && it.roll % 2 == 1 }.values.sum()
    val sndResult =
        this.filterKeys { it.score[1] >= i && it.score[0] < i && it.roll % 2 == 0 }.values.sum()
    return max(fstResult, sndResult)
}


val cachedCountWins = Memoized<State, Pair<Long, Long>> { countWinsCached(it) }

private fun countWinsCached(
    state: State,
): Pair<Long, Long> {
    if (state.score[0] >= 21) {
        return 1L to 0L
    } else if (state.score[1] >= 21) {
        return 0L to 1L
    }

    val occurrences = listOf(3 to 1, 4 to 3, 5 to 6, 6 to 7, 7 to 6, 8 to 3, 9 to 1)
    var result = 0L to 0L
    for ((move, frequency) in occurrences) {
        result += cachedCountWins(state.next(move)) * frequency.toLong()
    }
    return result

}

fun main() {
    solve("test", 3 to 7)
    solve("input", 6 to 5)
}

