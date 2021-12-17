package day17

import utils.x
import utils.y
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

fun main() {
    val test = (20 to 30) to (-10 to -5)
    solve(test, "test")
    val input = (138 to 184) to (-125 to -71)
    solve(input, "input")
}

fun constrainX(targetX: Pair<Int, Int>): IntRange {
    val minX = sqrt((targetX.first * 2).toDouble()).toInt() - 1
    val maxX = targetX.second
    return minX..maxX
}

fun constrainTime(initialVy: Int, targetY: Pair<Int, Int>): IntProgression {
    val minTime = 1
    val maxTime = if (initialVy < 0) {
        targetY.first/initialVy + 1
    } else {
        initialVy + sqrt(initialVy.toFloat()*initialVy - 2 * targetY.first).toInt() + 1
    }

    return minTime..maxTime
}

fun constrainY(targetY: Pair<Int, Int>): IntProgression {
    val miny = targetY.first
    val maxy = -targetY.first // consider such t that posY(t-1)==0, then posY(t)==-v_0- 1
    return miny..maxy
}

fun calculatePosY(initial: Int, time: Int): Int {
    return initial * time - time * (time - 1) / 2
}

fun calculatePosX(initial: Int, time: Int): Int {
    if (time >= initial) {
        return initial * (initial + 1) / 2
    }
    return time * (initial + initial - time + 1) / 2
}

fun solve(target: Pair<Pair<Int, Int>, Pair<Int, Int>>, dataset: String) {
    println("---- $dataset ----")

    val hits = mutableListOf<Int>()
    val initials = mutableSetOf<Pair<Int, Int>>()
    val timeInMillis = measureTimeMillis {
        for (vx in constrainX(target.x)) {
            for (vy in constrainY(target.y)) {
                for (dt in constrainTime(vy, target.y)) {
                    val posX = calculatePosX(vx, dt)
                    if (posX < target.x.first) continue
                    if (posX > target.x.second) continue
                    val posY = calculatePosY(vy, dt)
                    if (posY < target.y.first) continue
                    if (posY > target.y.second) continue
                    hits.add(vy)
                    initials.add(vx to vy)
                }
            }
        }

        val stage1result = hits.maxOf { it }.let { it * (it + 1) / 2 }
        println("Stage1: $stage1result")

        val stage2result = initials.size
        println("Stage2: $stage2result")
    }
    println("---- ${dataset} ---- Elapsed time: $timeInMillis")
}

