package day2

import java.io.File
import kotlin.system.measureTimeMillis

data class State(val depth: Int, val pos: Int, val aim: Int)

sealed class Instruction(val action: (State) -> State)
class F(val arg: Int) : Instruction({ it.copy(pos = it.pos + arg, depth = it.depth + arg * it.aim) })
class U(val arg: Int) : Instruction({ it.copy(aim = it.aim - arg) })
class D(val arg: Int) : Instruction({ it.copy(aim = it.aim + arg) })

fun main() {
    solve(File("src/day2/test.txt"))
    solve(File("src/day2/input.txt"))
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val instructions = file.readLines().map { line ->
        val opCode = line.dropLast(2)
        val arg = line.takeLast(1).toInt()

        when (opCode) {
            "up" -> U(arg)
            "down" -> D(arg)
            "forward" -> F(arg)
            else -> error("Unrecognized instruction")
        }
    }
    val timeInMillis = measureTimeMillis {
        val stage1result = instructions.fold(0 to 0) { acc, ins ->
            when (ins) {
                is F -> acc.first + ins.arg to acc.second
                is U -> acc.first to acc.second - ins.arg
                is D -> acc.first to acc.second + ins.arg
            }

        }.let { it.first * it.second }

        println("Stage1: $stage1result")

        val stage2result = execute(instructions)

        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

operator fun Pair<Int, Int>.times(arg: Int): Pair<Int, Int> = first * arg to second * arg
operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> = first + other.first to second + other.second
operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> = first - other.first to second - other.second


fun execute(instructions: List<Instruction>): Int {
    var ship = State(0, 0, 0)

    instructions.forEach {
        ship = it.action(ship)
    }
    return ship.depth * ship.pos
}
