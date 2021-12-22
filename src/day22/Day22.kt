package day22

import utils.x
import utils.y
import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.system.measureTimeMillis


data class Cube(
    val on: Boolean,
    val x: Pair<Int, Int>,
    val y: Pair<Int, Int>,
    val z: Pair<Int, Int>
)

fun Cube.small(): Boolean {
    return listOf(x.first, x.x, y.x, y.y, z.x, z.y).any { abs(it) <= 50 }
}

fun Cube.points(): Set<Triple<Int, Int, Int>> {
    val allPoints = HashSet<Triple<Int, Int, Int>>()

    for (i in x.x..x.y) {
        for (j in y.x..y.y) {
            for (k in z.x..z.y) {
                allPoints.add(Triple(i, j, k))
            }
        }
    }
    return allPoints
}

fun Cube.count(): Long {
    return (x.y - x.x + 1).toLong() * (y.y - y.x + 1) * (z.y - z.x + 1)
}

fun Cube.intersect(other: Cube): Cube? {
    if (other.x.y < x.x ||
        other.y.y < y.x ||
        other.z.y < z.x
    ) {
        return null
    }
    if (x.y < other.x.x ||
        y.y < other.y.x ||
        z.y < other.z.x
    ) {
        return null
    }
    return Cube(
        other.on, max(other.x.x, x.x) to min(other.x.y, x.y),
        max(other.y.x, y.x) to min(other.y.y, y.y),
        max(other.z.x, z.x) to min(other.z.y, z.y)
    )
}

fun Cube.cut(axis: Int, value: Int): List<Cube> {
    if (axis == 0) {
        if (value <= x.x || value > x.y)
            return listOf(this)
        return listOf(copy(on = on, x = x.x to value - 1), copy(on = on, x = value to x.y))
    } else if (axis == 1) {
        if (value <= y.x || value > y.y)
            return listOf(this)

        return listOf(copy(on = on, y = y.x to value - 1), copy(on = on, y = value to y.y))
    } else if (value <= z.x || value > z.y)
        return listOf(this)
    return listOf(copy(on = on, z = z.x to value - 1), copy(on = on, z = value to z.y))
}

fun Cube.merge(other: Cube): Set<Cube> {
    assert(this.on)
    var cubes = setOf(this)
    if (other.on) {
        cubes = setOf(this, other)
    }
    listOf(x.x, x.y + 1, other.x.x, other.x.y + 1).sorted().forEach {
        cubes = cubes.flatMap { cube -> cube.cut(0, it) }.toSet()
    }
    listOf(y.x, y.y + 1, other.y.x, other.y.y + 1).sorted().forEach {
        cubes = cubes.flatMap { cube -> cube.cut(1, it) }.toSet()
    }
    listOf(z.x, z.y + 1, other.z.x, other.z.y + 1).sorted().forEach {
        cubes = cubes.flatMap { cube -> cube.cut(2, it) }.toSet()
    }
//    cubes = cubes.flatMap {cube -> cube.cut(0, other.x.y+1) }.toSet()
//    cubes = cubes.flatMap {cube -> cube.cut(1, other.y.x) }.toSet()
//    cubes = cubes.flatMap {cube -> cube.cut(1, other.y.y+1) }.toSet()
//    cubes = cubes.flatMap {cube -> cube.cut(2, other.z.x) }.toSet()
//    cubes = cubes.flatMap {cube -> cube.cut(2, other.z.y+1) }.toSet()

    if (other.on) {
        for (c1 in cubes) {
            assert(c1.on)
            for (c2 in cubes) {
                if (c1 == c2) continue
                assert(c1.intersect(c2) == null)
            }
        }

        return cubes
    }
    cubes = cubes.filterNot { cube -> cube.inside(other) }.toSet()
    for (c1 in cubes) {
        assert(c1.on)
        for (c2 in cubes) {
            if (c1 == c2) continue
            assert(c1.intersect(c2) == null)
        }
    }
    return cubes
}

private fun Cube.inside(other: Cube): Boolean {
    if (other.x.x <= x.x && x.y <= other.x.y &&
        other.y.x <= y.x &&
        other.z.x <= z.x &&
        y.y <= other.y.y &&
        z.y <= other.z.y
    ) {
        return true
    }
    return false
}


fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val eol = System.lineSeparator()
    val cubes = file.readLines().map { line ->
        val result = """(\w+) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""".toRegex()
            .find(line)
//        val state = result!!.groups[1]
        val (state, minx, maxx, miny, maxy, minz, maxz) = result!!.destructured
        Cube(
            state == "on",
            minx.toInt() to maxx.toInt(),
            miny.toInt() to maxy.toInt(),
            minz.toInt() to maxz.toInt()
        )
    }

    println(cubes.first())


    val timeInMillis = measureTimeMillis {
        val sCubes = cubes.filter { it.small() }

        val result = sCubes.fold(HashSet<Triple<Int, Int, Int>>()) { acc, cube ->
            if (cube.on) {
                acc.addAll(cube.points())
            } else {
                acc.removeAll(cube.points())
            }
//            println(acc.size)
            acc
        }

        val stage1result = result.size
        println("Stage1:$stage1result")

        val part2Cubes = cubes

        val xcuts = part2Cubes.flatMap { listOf(it.x.x, it.x.y, it.x.y+1) }.sorted().distinct()
        val ycuts = part2Cubes.flatMap { listOf(it.y.x, it.y.y, it.y.y+1) }.sorted().distinct()
        val zcuts = part2Cubes.flatMap { listOf(it.z.x, it.z.y, it.z.y+1) }.sorted().distinct()
//        val xcuts = smallCubes.flatMap { listOf(it.x.x, it.x.y) }.sorted().let { cuts -> cuts + (cuts.last() + 1)}
//        val ycuts = smallCubes.flatMap { listOf(it.y.x, it.y.y) }.sorted().let { cuts -> cuts + (cuts.last() + 1)}
//        val zcuts = smallCubes.flatMap { listOf(it.z.x, it.z.y) }.sorted().let { cuts -> cuts + (cuts.last() + 1)}
        println(xcuts)
        println(ycuts)
        println(zcuts)
        val total = xcuts.size.toLong()*ycuts.size*zcuts.size
        println(total)
        var counter = 0L
        var turnedOn = 0L
        for (xrange in xcuts.zipWithNext()) {
            for (yrange in ycuts.zipWithNext()) {
                for (zrange in zcuts.zipWithNext()) {
                    val tested = Cube(true, xrange.x to xrange.y - 1, yrange.x to yrange.y -1, zrange.x to zrange.y - 1)
                    counter += 1L
                    if (counter % 1048576.toLong() == 0L) {
                        println("$counter/$total")
                    }
                    val important = part2Cubes.filter { tested.inside(it)}
                    if (important.any { it.on} && important.last().on) {
                        turnedOn += tested.count()
//                        println("${tested.count()} $tested")
                    } else {
//                        for (i in tested.x.x..tested.x.y) {
//                            for (j in tested.y.x..tested.y.y) {
//                                for (k in tested.z.x .. tested.z.y) {
//                                    assert(!result.contains(Triple(i,j,k)))
//                                }
//                            }
//                        }
                    }
                }
            }
        }

        //2758514936282235
        //2758514936282235
        println(turnedOn)
        val stage2result = turnedOn


        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

//private fun Set<Cube>.removeIntersections(): Set<Cube> {
//    var cubes = this.toMutableSet()
//    for (c1 in cubes) {
//        assert(c1.on)
//        for (c2 in cubes) {
//            assert(c2.on)
//            if (c1 == c2) continue
//            if (c1.intersect(c2) == null) continue
//            cubes.remove(c1)
//            cubes.remove(c2)
//            cubes.addAll(c1.merge(c2))
//            return cubes.removeIntersections()
//
//        }
//    }
//    return cubes
//}

//1214313344725528
fun main() {
    solve(File("src/day22/test.txt"))
    solve(File("src/day22/input.txt"))
}

