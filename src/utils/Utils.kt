package utils

import java.util.*
import kotlin.collections.HashSet

operator fun Pair<Int, Int>.times(arg: Int): Pair<Int, Int> = first * arg to second * arg

operator fun Pair<Long, Long>.times(arg: Long): Pair<Long, Long> = first * arg to second * arg

@JvmName("plusIntInt")
operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
    first + other.first to second + other.second

@JvmName("plusLongLong")
operator fun Pair<Long, Long>.plus(other: Pair<Long, Long>): Pair<Long, Long> =
    first + other.first to second + other.second

operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> =
    first - other.first to second - other.second

infix fun Int.toward(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}

fun String.permutations(): List<String> {
    if (length == 1)
        return listOf(this)
    return (this.indices).flatMap { i ->
        removeRange(i, i + 1).permutations().map { it + this[i] }
    }
}

val <E> Pair<E, E>.y: E
    get() = this.second
val <E> Pair<E, E>.x: E
    get() = this.first

val <E> List<List<E>>.gridSize: Pair<Int, Int>
    get() = this.size to this[0].size

fun List<List<Int>>.adjacent(
    point: Pair<Int, Int>,
    hov: Boolean = true,
    diag: Boolean = false
): List<Pair<Int, Int>> =
    adjacent(point.x, point.y, hov, diag)

fun List<List<Int>>.adjacent(
    x: Int,
    y: Int,
    hov: Boolean = true,
    diag: Boolean = false
): List<Pair<Int, Int>> {
    val hovSteps = if (hov) listOf(1 to 0, 0 to 1, -1 to 0, 0 to -1) else emptyList()
    val diagSteps = if (diag) listOf(1 to 1, -1 to 1, -1 to -1, 1 to -1) else emptyList()
    return (hovSteps + diagSteps).map { step -> x + step.first to y + step.second }
        .filter { (x, y) ->
            x >= 0 && y >= 0 && x < size && y < this[0].size
        }
}

inline fun List<List<Int>>.gridForEach(action: (Int) -> Unit) {
    for (row in this)
        for (element in row)
            action(element)
}

inline fun <T> List<List<T>>.gridForEachIndexed(action: (i: Int, j: Int, T) -> Unit) {
    this.forEachIndexed { i, row ->
        row.forEachIndexed { j, item ->
            action(i, j, item)
        }
    }
}

inline fun <T, R> List<List<T>>.gridMap(transform: (T) -> R): List<List<R>> {
    return this.map { row ->
        row.map { item ->
            transform(item)
        }
    }
}

inline fun <T, R> List<List<T>>.mutableGridMap(transform: (T) -> R): List<MutableList<R>> {
    return this.map { row ->
        row.map { item ->
            transform(item)
        }.toMutableList()
    }
}

inline fun <T, R> List<List<T>>.gridMapIndexed(transform: (i: Int, j: Int, T) -> R): List<List<R>> {
    return this.mapIndexed { i, row ->
        row.mapIndexed { j, item ->
            transform(i, j, item)
        }
    }
}

inline fun <T, R> List<List<T>>.gridMapIndexed(transform: (point: Pair<Int, Int>, T) -> R): List<List<R>> {
    return this.mapIndexed { i, row ->
        row.mapIndexed { j, item ->
            transform(i to j, item)
        }
    }
}

fun <E> List<List<E>>.getValue(point: Pair<Int, Int>): E = this[point.x][point.y]
fun <E> List<MutableList<E>>.setValue(point: Pair<Int, Int>, value: E) {
    this[point.x][point.y] = value
}

fun List<List<Int>>.calculateShortestPath(start: Pair<Int, Int>, end: Pair<Int, Int>): Int =
    dijkstra(start).path(end).sumOf { getValue(it) }

fun List<List<Pair<Int, Int>?>>.path(end: Pair<Int, Int>): List<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()
    var current = end
    while (this.getValue(current) != null) {
        result.add(current)
        current = getValue(current)!!
    }

    return result.toList()
}

fun List<List<Int>>.dijkstra(start: Pair<Int, Int>): List<List<Pair<Int, Int>?>> {
    val visited: HashSet<Pair<Int, Int>> = HashSet()

    val delta = mutableGridMap { Int.MAX_VALUE }
    val comparator: Comparator<Pair<Int, Int>> = compareBy { delta.getValue(it) }

    val queue = PriorityQueue(size * this[0].size, comparator)
    delta.setValue(start, 0)
    queue.add(start)

    val previous: List<MutableList<Pair<Int, Int>?>> = mutableGridMap { null }

    while (queue.isNotEmpty()) {
        val next: Pair<Int, Int> = queue.peek()

        adjacent(next).minus(visited).forEach { neighbor ->
            val newPath = delta.getValue(next) + getValue(neighbor)

            if (newPath < delta.getValue(neighbor)) {
                queue.remove(neighbor)
                delta.setValue(neighbor, newPath)
                queue.add(neighbor)
                previous.setValue(neighbor, next)
            }
        }

        visited.add(next)
        queue.remove()
    }

    return previous.map { it.toList() }
}

class Memoized<X,R>(val fn: (X)-> R) {
    private val cache: MutableMap<X, R> = HashMap()
    operator fun invoke(x: X) : R {
        return cache.getOrPut(x) { fn(x) }
    }
}

