package utils

operator fun Pair<Int, Int>.times(arg: Int): Pair<Int, Int> = first * arg to second * arg
operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> = first + other.first to second + other.second
operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> = first - other.first to second - other.second

