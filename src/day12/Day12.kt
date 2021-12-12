package day12

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    solve(File("src/day12/test.txt"))
    solve(File("src/day12/input.txt"))
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val paths = file.readLines().map { line -> line.split('-').let { it.first() to it.last() } }

    val graph = mutableMapOf<String, Set<String>>().withDefault { setOf() }
    paths.forEach {
        graph[it.first] = graph.getValue(it.first).plus(it.second)
        graph[it.second] = graph.getValue(it.second).plus(it.first)
    }

    val timeInMillis = measureTimeMillis {
        val stage1result = graph.findPaths("start", "end", allowRedo = false).size
        println("Stage1: $stage1result")

        val stage2result = graph.findPaths("start", "end", allowRedo = true).size
        println("Stage2: $stage2result")
    }

    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}

fun Map<String, Set<String>>.removeNode(node: String): Map<String, Set<String>> {
    return this.filterNot { it.key == node }.mapValues { (k, v) -> v.minus(node) }
}

fun Map<String, Set<String>>.findPaths(
    start: String,
    end: String,
    allowRedo: Boolean,
    path: List<String> = emptyList()
): Set<List<String>> {
    val result = mutableSetOf<List<String>>()
    val newPath = path.plus(start)
    getValue(start).forEach { newStart ->
        if (newStart == end) { // found
            result.add(newPath.plus(end))
        } else if (start.uppercase() == start) { // is big cave
            result.addAll(findPaths(newStart, end, allowRedo, newPath))
        } else { // small cave
            result.addAll(removeNode(start).findPaths(newStart, end, allowRedo, newPath))
            if (allowRedo && start != "start") { // use redo if available
                result.addAll(findPaths(newStart, end, false, newPath))
            }
        }
    }

    return result.toSet()
}