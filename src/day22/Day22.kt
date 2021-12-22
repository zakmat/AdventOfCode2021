package day22

import utils.lower
import utils.upper
import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.system.measureTimeMillis


data class Cube(val coords: List<Pair<Int, Int>>)

fun Cube.small() = coords.flatMap { listOf(it.lower, it.upper) }.any { abs(it) <= 50 }

fun Cube.points(): Set<Triple<Int, Int, Int>> {
    val allPoints = HashSet<Triple<Int, Int, Int>>()

    for (i in coords[0].lower..coords[0].upper) {
        for (j in coords[1].lower..coords[1].upper) {
            for (k in coords[2].lower..coords[2].upper) {
                allPoints.add(Triple(i, j, k))
            }
        }
    }
    return allPoints
}

fun Cube.count() = coords.fold(1L) { acc, it -> acc * (it.upper - it.lower + 1) }

fun Cube.intersect(other: Cube): Cube? {
    if (coords.zip(other.coords).any { (firstCube, secondCube) ->
            firstCube.upper < secondCube.lower || secondCube.upper < firstCube.lower
        }) {
        return null
    }
    return Cube(
        coords.zip(other.coords)
            .map { (firstCube, secondCube) ->
                max(firstCube.lower, secondCube.lower) to min(firstCube.upper, secondCube.upper)
            }
    )
}

fun Cube.cut(axis: Int, cuts: Pair<Int, Int>): List<Cube> {
    val slices = mutableListOf<Cube>()
    if (coords[axis].upper < cuts.first || cuts.second < coords[axis].lower) {
        assert(false) { "Shall be intersecting" }
        return listOf(this)
    }
    slices.add(copy(coords = coords.mapIndexed { i, old -> if (i == axis) cuts else old }))
    if (coords[axis].lower < cuts.first) {
        slices.add(copy(coords = coords.mapIndexed { i, old -> if (i == axis) coords[axis].lower to cuts.first - 1 else old }))
    }
    if (cuts.second < coords[axis].upper) {
        slices.add(copy(coords = coords.mapIndexed { i, old -> if (i == axis) cuts.second + 1 to coords[axis].upper else old }))
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

    val timeInMillis = measureTimeMillis {
        val sCubes = steps.filter { it.second.small() }

        val stage1result = sCubes.fold(emptySet<Triple<Int, Int, Int>>()) { acc, (on, cube) ->
            if (on) {
                acc.union(cube.points())
            } else {
                acc.subtract(cube.points())
            }
        }.size
        println("Stage1:$stage1result")

        val stage2result = steps.fold(emptyList<Cube>()) { onCubes, (on, candidateCube) ->
            if (on) {
                onCubes + onCubes.union(candidateCube)
            } else {
                onCubes.flatMap { it.difference(candidateCube) }
            }
        }.sumOf { it.count() }
        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

//1214313344725528
fun main() {
    solve(File("src/day22/test.txt"))
    solve(File("src/day22/input.txt"))
}

