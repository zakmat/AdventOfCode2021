package day22

import utils.x
import utils.y
import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.system.measureTimeMillis


data class Cube(val coords: List<Pair<Int, Int>>)

fun Cube.small() = coords.flatMap { listOf(it.x, it.y) }.any { abs(it) <= 50 }

fun Cube.points(): Set<Triple<Int, Int, Int>> {
    val allPoints = HashSet<Triple<Int, Int, Int>>()

    for (i in coords[0].x..coords[0].y) {
        for (j in coords[1].x..coords[1].y) {
            for (k in coords[2].x..coords[2].y) {
                allPoints.add(Triple(i, j, k))
            }
        }
    }
    return allPoints
}

fun Cube.count() = coords.fold(1L) { acc, it -> acc * (it.y - it.x + 1) }

fun Cube.intersect(other: Cube): Cube? {
    if (other.coords[0].y < coords[0].x || coords[0].y < other.coords[0].x ||
        other.coords[1].y < coords[1].x || coords[1].y < other.coords[1].x ||
        other.coords[2].y < coords[2].x || coords[2].y < other.coords[2].x
    ) {
        return null
    }
    return Cube(
        listOf(
            max(other.coords[0].x, coords[0].x) to min(other.coords[0].y, coords[0].y),
            max(other.coords[1].x, coords[1].x) to min(other.coords[1].y, coords[1].y),
            max(other.coords[2].x, coords[2].x) to min(other.coords[2].y, coords[2].y)
        )
    )
}

fun Cube.cut(axis: Int, cuts: Pair<Int, Int>): List<Cube> {
    val slices = mutableListOf<Cube>()
    if (coords[axis].y < cuts.first || cuts.second < coords[axis].x) {
        assert(false) { "Shall be intersecting" }
        return listOf(this)
    }
    slices.add(copy(coords = coords.mapIndexed { i, it -> if (i == axis) cuts else it }))
    if (coords[axis].x < cuts.first) {
        slices.add(copy(coords = coords.mapIndexed { i, it -> if (i == axis) coords[axis].x to cuts.first - 1 else it }))
    }
    if (cuts.second < coords[axis].y) {
        slices.add(copy(coords = coords.mapIndexed { i, it -> if (i == axis) cuts.second + 1 to coords[axis].y else it }))
    }
    return slices
}

fun List<Cube>.union(other: Cube): List<Cube> {

    var candidateParts = mutableListOf(other)
    for (cube in this) {
        val nextCandidateParts = mutableListOf<Cube>()
        candidateParts.forEach { nextCandidateParts.addAll(it.difference(cube)) }
        candidateParts = nextCandidateParts
        if (candidateParts.isEmpty())
            break
    }
    return candidateParts
}

fun Cube.difference(other: Cube): List<Cube> {
    val intersection = this.intersect(other) ?: return listOf(this)

    var slicedCube = this
    val cubes = mutableListOf<Cube>()

    for (i in 0..2) {
        slicedCube.cut(i, intersection.coords[i])
            .let { slicedCube = it.first(); cubes.addAll(it.drop(1)) }
    }

    assert(intersection == slicedCube)
    assert(cubes.size < 7)

    return cubes
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val steps = file.readLines().map { line ->
        val result = """(\w+) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""".toRegex()
            .find(line)
        val (state, minX, maxX, minY, maxY, minZ, maxZ) = result!!.destructured
        (state == "on") to
                Cube(
                    listOf(
                        minX.toInt() to maxX.toInt(),
                        minY.toInt() to maxY.toInt(),
                        minZ.toInt() to maxZ.toInt()
                    )
                )
    }

    println(steps.first())


    val timeInMillis = measureTimeMillis {
        val sCubes = steps.filter { it.second.small() }

        val result = sCubes.fold(HashSet<Triple<Int, Int, Int>>()) { acc, (on, cube) ->
            if (on) {
                acc.addAll(cube.points())
            } else {
                acc.removeAll(cube.points())
            }
            acc
        }

        val stage1result = result.size
        println("Stage1:$stage1result")

        var onCubes = mutableListOf(steps.first().second)
        for ((on, candidateCube) in steps) {
            if (on) {
                onCubes.addAll(onCubes.union(candidateCube))
            } else {
                onCubes = onCubes.flatMap { it.difference(candidateCube) }.toMutableList()
            }
        }

        val stage2result = onCubes.sumOf { it.count() }
        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

//1214313344725528
fun main() {
    solve(File("src/day22/test.txt"))
    solve(File("src/day22/input.txt"))
}

