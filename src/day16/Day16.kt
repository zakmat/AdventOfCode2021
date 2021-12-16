package day16

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day16/test.txt"))
    solve(File("src/day16/input.txt"))
}

sealed class Packet(val version: Int, val type: Int)

data class Literal(val header: String, val nibbles: List<Int>) : Packet(header.take(3).toInt(2), header.drop(3).take(3).toInt(2))
data class Operator(val header: String, val subpackets: List<Packet>) : Packet(header.take(3).toInt(2), header.drop(3).take(3).toInt(2))

fun Packet.eval(): Long = when (this) {
    is Operator -> {
        this.eval()
    }
    is Literal -> {
        this.eval()
    }
}

fun Literal.eval() : Long {
    return nibbles.fold(0) { acc, nibble -> acc * 16 + nibble }
}

fun Operator.eval() : Long {
    val type = header.drop(3).take(3).toInt(2)
//    println("Operation $type on ${subpackets.map {it.eval()}}")
    return when (type) {
        0 -> subpackets.sumOf { it.eval()}
        1 -> subpackets.fold(1) { acc, item -> acc * item.eval() }
        2 -> subpackets.minOf { it.eval() }
        3 -> subpackets.maxOf { it.eval() }
        5 -> if (subpackets.first().eval() > subpackets.last().eval()) 1 else 0
        6 -> if (subpackets.first().eval() < subpackets.last().eval()) 1 else 0
        7 -> if (subpackets.first().eval() == subpackets.last().eval()) 1 else 0
        else -> error("Unexpected operator type $type")
    }
}

fun String.parseLiteral(): Pair<Literal, String> {
    val header = take(6)
    var rest = drop(6)
    var isFinished = false
    val nibbles = mutableListOf<Int>()
    while(!isFinished) {
        val dectet = rest.take(5)
        rest = rest.drop(5)
        isFinished = dectet[0] == '0'
        nibbles.add(dectet.drop(1).toInt(2))
    }
//    println("Parsed literal")
    return Literal(header, nibbles.toList()) to rest
}

fun String.parseOperator(): Pair<Operator, String> {
    val header = take(7)
    val lengthType = header.takeLast(1)
    return if (lengthType == "0") {
        val size = drop(7).take(15).toInt(2)
        val subpackets = drop(22).take(size).parseExact()
//        println("Parsed operator")
        Operator(header, subpackets) to drop(22+size)
    } else {
        val num = drop(7).take(11).toInt(2)
        val (subpackets, rest) = drop(18).parseN(num)
//        println("Parsed operator")
        Operator(header, subpackets) to rest
    }
}

fun String.parseFirst() : Pair<Packet, String> {
    val typeId = this.drop(3).take(3).toInt(2)
    return when (typeId) {
        4 -> parseLiteral()
        else -> parseOperator()
    }
}

fun String.parseExact() : List<Packet> {
    var dataLeft = this
    val packets = mutableListOf<Packet>()
    while(dataLeft.isNotEmpty()) {
        val (packet, rest) = dataLeft.parseFirst()
//        println("Parsed $packet")
        packets.add(packet)
        dataLeft = rest
    }
    return packets.toList()
}

fun String.parseN(num: Int) : Pair<List<Packet>, String> {
    var current = 0
    val packets = mutableListOf<Packet>()
    var dataLeft = this
    while(current++ < num) {
        val (packet, rest) = dataLeft.parseFirst()
//        println("Parsed $packet")
        packets.add(packet)
        dataLeft = rest
    }
    return packets.toList() to dataLeft
}

fun Packet.countVersions(): Int {
    if (this is Literal) {
        return this.version
    }
    else {
        return this.version + (this as Operator).subpackets.sumOf { it.countVersions()}
    }
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val message = file.readText().trim().toList().map {it.digitToInt(16)}
        .map { it.toString(2).padStart(4,'0') }.joinToString("")

    val timeInMillis = measureTimeMillis {
        val (packet, zeros) = message.parseFirst()
        val stage1result = packet.countVersions()
        println("Stage1: $stage1result")

        val stage2result = packet.eval()
        println("Stage2: $stage2result")

    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}


