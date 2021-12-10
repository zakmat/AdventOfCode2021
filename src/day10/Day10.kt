package day10

import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day10/test.txt"))
    solve(File("src/day10/input.txt"))
}

fun completion(line: String): Long? {
    val stack = ArrayDeque<Char>()
    line.forEach {
        when (it) {
            '(', '{', '[', '<' -> stack.push(it)
            ')' -> {
                val char = stack.pop()
                if (char != '(') return null
            }
            '}' -> {
                val char = stack.pop()
                if (char != '{') return null
            }
            ']' -> {
                val char = stack.pop()
                if (char != '[') return null
            }
            '>' -> {
                val char = stack.pop()
                if (char != '<') return null
            }
        }
    }
    val result = mutableListOf<Char>()
    while (stack.isNotEmpty()) {
        result.add(stack.pop())
    }

    var totalScore = 0L
    result.forEach {
        totalScore = totalScore * 5 + charScore(it)
    }

    return totalScore
}

fun charScore(it: Char): Long = when (it) {
    '(' -> 1L
    '[' -> 2L
    '{' -> 3L
    '<' -> 4L
    else -> error("Unrecognized: $it")
}

fun corrupted(line: String): Char? {
    val stack = ArrayDeque<Char>()
    line.forEach {
        when (it) {
            '(', '{', '[', '<' -> stack.push(it)
            ')' -> {
                val char = stack.pop()
                if (char != '(') return it
            }
            '}' -> {
                val char = stack.pop()
                if (char != '{') return it
            }
            ']' -> {
                val char = stack.pop()
                if (char != '[') return it
            }
            '>' -> {
                val char = stack.pop()
                if (char != '<') return it
            }
        }
    }
    return null
}


fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val lines = file.readLines()

    val timeInMillis = measureTimeMillis {

        val stage1result = lines.mapNotNull { corrupted(it) }.sumOf { char ->
            when (char) {
                ')' -> 3L
                '}' -> 1197L
                ']' -> 57L
                '>' -> 25137L
                else -> error("Unrecognized")
            }
        }
        println("Stage1: $stage1result")


        val stage2result = lines.mapNotNull { completion(it) }.sorted().let { it[it.size/2]}
        println("Stage2: $stage2result")
    }

    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}
