package day16

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day16/test.txt"))
    solve(File("src/day16/input.txt"))
}

class Packet(
    private val version: Int,
    private val type: Int,
    private val operands: List<Packet> = emptyList(),
    private val value: Long? = null
) {
    fun countVersions(): Int = this.version + operands.sumOf { it.countVersions() }

    fun eval(): Long {
        return when (type) {
            0 -> operands.sumOf { it.eval() }
            1 -> operands.fold(1) { acc, item -> acc * item.eval() }
            2 -> operands.minOf { it.eval() }
            3 -> operands.maxOf { it.eval() }
            4 -> value!!
            5 -> if (operands.first().eval() > operands.last().eval()) 1 else 0
            6 -> if (operands.first().eval() < operands.last().eval()) 1 else 0
            7 -> if (operands.first().eval() == operands.last().eval()) 1 else 0
            else -> error("Unexpected type")
        }
    }
}

fun parseSequence(incoming: String): Sequence<Packet> = parseSequence(incoming.iterator())

fun parseSequence(iterator: CharIterator): Sequence<Packet> = sequence {
    while (iterator.hasNext()) {
        val version = iterator.take(3).toInt(2)
        val type = iterator.take(3).toInt(2)
        if (type == 4) {
            val value = iterator.parseLiteralValue()
            yield(Packet(version, type, value = value))
        } else {
            val lengthType = iterator.next()
            if (lengthType == '0') {
                val size = iterator.take(15).toInt(2)
                val operands = parseSequence(iterator.take(size)).toList()
                yield(Packet(version, type, operands))
            } else {
                val num = iterator.take(11).toInt(2)
                val operands = parseSequence(iterator).take(num).toList()
                yield(Packet(version, type, operands))
            }
        }
    }
}

private fun CharIterator.parseLiteralValue() = sequence {
    while (take(1) == "1") {
        yield(take(4))
    }
    yield(take(4))
}.map { it.toInt(2) }.fold(0L) { acc, nibble -> acc * 16 + nibble }

private fun CharIterator.take(i: Int): String = (0 until i).map { next() }.joinToString("")

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val binaryMessage = file.readText().trim().toList().map { it.digitToInt(16) }
        .joinToString("") { it.toString(2).padStart(4, '0') }

    val timeInMillis = measureTimeMillis {
        val packet = parseSequence(binaryMessage).first()
        val stage1result = packet.countVersions()
        println("Stage1: $stage1result")

        val stage2result = packet.eval()
        println("Stage2: $stage2result")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}


