package utils

operator fun Pair<Int, Int>.times(arg: Int): Pair<Int, Int> = first * arg to second * arg
operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
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

val Pair<Int, Int>.y: Int
    get() = this.second
val Pair<Int, Int>.x: Int
    get() = this.first

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
