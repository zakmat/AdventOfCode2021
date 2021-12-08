package utils

operator fun Pair<Int, Int>.times(arg: Int): Pair<Int, Int> = first * arg to second * arg
operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> = first + other.first to second + other.second
operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> = first - other.first to second - other.second

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
