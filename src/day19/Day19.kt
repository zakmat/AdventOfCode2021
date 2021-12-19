package day19

import java.io.File
import kotlin.math.abs
import kotlin.system.measureTimeMillis

typealias Position = Triple<Int, Int, Int>
typealias Vector = Triple<Int, Int, Int>
typealias Scanner = Set<Position>
typealias ScannerRels = Set<Triple<Vector, Position, Position>>
typealias Rotation = List<Int>

operator fun Position.minus(other: Position) =
    Vector(first - other.first, second - other.second, third - other.third)

fun Vector.rotate(m: Rotation): Vector {
    return Vector(
        m[0] * first + m[1] * second + m[2] * third,
        m[3] * first + m[4] * second + m[5] * third,
        m[6] * first + m[7] * second + m[8] * third,
    )
}

fun Position.manhattan(other: Position) =
    abs(other.first - first) + abs(other.second - second) + abs(other.third - third)

val transformations = setOf<Rotation>(
    // i,j,k in {0..4} -> setOf(RotX^i*RotY^j*RotX^k)
    listOf(0, 0, -1, 1, 0, 0, 0, -1, 0),
    listOf(0, 0, -1, 0, 1, 0, 1, 0, 0),
    listOf(0, 1, 0, 0, 0, -1, -1, 0, 0),
    listOf(0, 0, 1, 0, -1, 0, 1, 0, 0),
    listOf(-1, 0, 0, 0, 0, -1, 0, -1, 0),
    listOf(0, 0, 1, -1, 0, 0, 0, -1, 0),
    listOf(-1, 0, 0, 0, 1, 0, 0, 0, -1),
    listOf(0, -1, 0, 0, 0, 1, -1, 0, 0),
    listOf(1, 0, 0, 0, -1, 0, 0, 0, -1),
    listOf(0, 0, 1, 0, 1, 0, -1, 0, 0),
    listOf(1, 0, 0, 0, 0, 1, 0, -1, 0),
    listOf(1, 0, 0, 0, 1, 0, 0, 0, 1),
    listOf(0, 1, 0, 1, 0, 0, 0, 0, -1),
    listOf(-1, 0, 0, 0, 0, 1, 0, 1, 0),
    listOf(0, 0, -1, 0, -1, 0, -1, 0, 0),
    listOf(0, 1, 0, -1, 0, 0, 0, 0, 1),
    listOf(-1, 0, 0, 0, -1, 0, 0, 0, 1),
    listOf(0, 0, 1, 1, 0, 0, 0, 1, 0),
    listOf(0, 1, 0, 0, 0, 1, 1, 0, 0),
    listOf(0, 0, -1, -1, 0, 0, 0, 1, 0),
    listOf(0, -1, 0, -1, 0, 0, 0, 0, -1),
    listOf(1, 0, 0, 0, 0, -1, 0, 1, 0),
    listOf(0, -1, 0, 0, 0, -1, 1, 0, 0),
    listOf(0, -1, 0, 1, 0, 0, 0, 0, 1)
)

@JvmName("performShiftS")
fun Scanner.performShift(shift: Position): Scanner {
    return this.map { it - shift }.toSet()
}

@JvmName("performShiftSR")
fun ScannerRels.performShift(shift: Position): ScannerRels {
    return this.map {
        Triple(it.first, it.second - shift, it.third - shift)
    }.toSet()
}

@JvmName("performRotateS")
fun Scanner.performRotate(transformation: Rotation): Scanner {
    return this.map { it.rotate(transformation) }.toSet()
}

@JvmName("performRotateSR")
fun ScannerRels.performRotate(transformation: Rotation): ScannerRels {
    return this.map {
        Triple(
            it.first.rotate(transformation),
            it.second.rotate(transformation),
            it.third.rotate(transformation)
        )
    }.toSet()
}

fun findPositionAndOrientation(first: ScannerRels, second: ScannerRels): Pair<Rotation, Vector>? {
    for (t in transformations) {
        val common =
            first.map { it.first }.intersect(second.map { it.first.rotate(t) }.toSet()).size

        if (common == 132) { // 12 * 11
            val shift = findShift(first, second.performRotate(t))
            return t to shift
        } else if (common > 132) {
            error("Bigger: $common")
        }
    }
    return null
}

fun findShift(first: ScannerRels, second: ScannerRels): Vector {
    return first.firstNotNullOf { f ->
        second.firstNotNullOfOrNull { s ->
            if (f.first == s.first) {
                s.second - f.second
            } else {
                null
            }
        }
    }
}


fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val eol = System.lineSeparator()
    val scanners = file.readText().trim().split("$eol$eol").map { scanner ->
        scanner.split(eol).drop(1)
            .map { line ->
                line.split(',').map(String::toInt).let { Position(it[0], it[1], it[2]) }
            }
            .toSet()
    }.toMutableList()

    val timeInMillis = measureTimeMillis {
        val relatives = scanners.map { scanner ->

            scanner.flatMapIndexed { i, fstBeacon ->
                scanner.mapIndexed { j, sndBeacon ->
                    if (i == j) null else {
                        Triple(sndBeacon - fstBeacon, fstBeacon, sndBeacon)
                    }
                }.filterNotNull()
            }.toSet()
        }.toMutableList()

        val alreadyFound = mutableSetOf(0)
        val scannerPositions = MutableList(scanners.size) { Position(0, 0, 0) }

        while (alreadyFound.size < scanners.size) {
            for (i in scanners.indices) {
                if (!alreadyFound.contains(i))
                    continue
                for (j in scanners.indices) {
                    if (j in alreadyFound)
                        continue
                    val knownRelatives = relatives[i]
                    val candidateRelatives = relatives[j]
                    findPositionAndOrientation(
                        knownRelatives,
                        candidateRelatives
                    )?.let { (rotation, shift) ->
                        scanners[j] = scanners[j].performRotate(rotation).performShift(shift)
                        relatives[j] = relatives[j].performRotate(rotation).performShift(shift)
                        alreadyFound.add(j)
                        scannerPositions[j] = shift
                    }
                }
            }
        }

        println("Stage1: ${scanners.reduce { acc, beacons -> acc.union(beacons) }.size}")

        val stage2result = scannerPositions.maxOf { fst ->
            scannerPositions.maxOf { snd ->
                fst.manhattan(snd)
            }
        }
        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

fun main() {
    solve(File("src/day19/test.txt"))
    solve(File("src/day19/input.txt"))
}

