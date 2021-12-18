package day18

import java.io.File
import java.lang.Integer.max
import kotlin.system.measureTimeMillis

sealed class Node {
    abstract fun magnitude(): Int
}

data class Branch(var left: Node, var right: Node) : Node() {
    override fun magnitude(): Int = 3 * left.magnitude() + 2 * right.magnitude()
    override fun toString(): String = "[$left,$right]"
}

data class Leaf(val regular: Int) : Node() {
    override fun magnitude() = regular
    override fun toString(): String = regular.toString()
}

fun add(first: Node, other: Node): Node {
    return Branch(left = first, right = other).reduce()
}

fun parent(top: Node, node: Node): Branch? {
    if (top is Branch) {
        if (top.left === node) {
            return top
        }
        if (top.right === node) {
            return top
        }
        return parent(top.left, node) ?: parent(top.right, node)
    }
    return null
}

fun prev(top: Branch, node: Node): Leaf? {
    val parent = parent(top, node) ?: return null
    if (parent.right === node) {
        return parent.left.last()
    }
    if (parent.left === node) {
        return prev(top, parent)
    }
    return null
}

fun next(top: Branch, node: Node): Leaf? {
    val parent = parent(top, node) ?: return null
    if (parent.left === node) {
        return parent.right.first()
    }
    if (parent.right === node) {
        return next(top, parent)
    }
    return null
}

fun Node.first(): Leaf {
    if (this is Branch) {
        return if (left is Branch) {
            left.first()
        } else {
            left as Leaf
        }
    }
    return this as Leaf
}

fun Node.last(): Leaf {
    if (this is Branch) {
        return if (right is Branch) {
            right.last()
        } else {
            right as Leaf
        }
    }
    return this as Leaf
}

fun Branch.reduce(): Node {
    while (true) {
        val explodeTarget = this.findExplodeReduction(level = 0)
        if (explodeTarget != null) {
            val prev = prev(this, explodeTarget)
            val next = next(this, explodeTarget)
            val result = explodeTarget.explode(prev, next)
            val originals = listOf(explodeTarget, prev(this, explodeTarget), next(this, explodeTarget))
            originals.zip(result).forEach { (old, new) ->
                if (old != null && new != null) {
                    this.replace(old, new)
                }
            }
            continue
        }
        val splitTarget = this.findSplitReduction()
        if (splitTarget != null) {
            this.replace(splitTarget, splitTarget.split())
            continue
        }
        return this
    }
}

private fun Branch.replace(old: Node, new: Node) {
    val top = this
    val targetParent = parent(top, old) ?: error("Failed to found old in the whole hierarchy")
    if (targetParent.left === old) {
        targetParent.left = new
    } else if (targetParent.right === old) {
        targetParent.right = new
    }
}

fun Branch.canExplode(level: Int): Boolean = level >= 4 && left is Leaf && right is Leaf
fun Node.findExplodeReduction(level: Int): Branch? {
    if (this is Branch) {
        if (canExplode(level))
            return this
        return left.findExplodeReduction(level + 1) ?: right.findExplodeReduction(level + 1)
    }
    return null
}

fun Leaf.canSplit(): Boolean = regular >= 10
fun Node.findSplitReduction(): Leaf? {
    if (this is Leaf) {
        if (canSplit())
            return this
    } else if (this is Branch) {
        return left.findSplitReduction() ?: right.findSplitReduction()
    }
    return null
}

fun Leaf.split(): Branch {
    return Branch(
        left = Leaf(regular / 2),
        right = Leaf(if (regular % 2 == 1) regular / 2 + 1 else regular / 2)
    )
}

fun Branch.explode(previous: Leaf?, next: Leaf?): List<Leaf?> {
    return listOf(
        Leaf(0),
        previous?.copy(regular = (left as Leaf).regular + previous.regular) ?: previous,
        next?.copy(regular = (right as Leaf).regular + next.regular) ?: next
    )
}

fun parseNode(iterator: CharIterator, level: Int): Node {
    var left: Node? = null
    var right: Node? = null
    var current: Int? = null
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (next == '[') {
            if (left == null) {
                left = parseNode(iterator, level + 1)
            } else {
                right = parseNode(iterator, level + 1)
            }
        } else if (next == ',') {
            if (current != null) {
                if (left == null) {
                    left = Leaf(current)
                } else {
                    right = Leaf(current)
                }
                current = null
            }
        } else if (next == ']') {
            if (current != null) {
                if (left == null) {
                    left = Leaf(current)
                } else {
                    right = Leaf(current)
                }
            }
            return Branch(left = left!!, right = right!!)
        } else {
            current = (current ?: 0) * 10 + next.digitToInt()
        }
    }
    error("End of stream")
}

fun main() {
    solve(File("src/day18/test.txt"))
    solve(File("src/day18/input.txt"))
}

fun solve(file: File) {
    println("---- ${file.nameWithoutExtension} ----")
    val lines = file.readLines()

    val timeInMillis = measureTimeMillis {
        val nodes = lines.map { line ->
            val iter = line.iterator()
            iter.next()
            parseNode(iter, 0)
        }
        val stage1result = nodes.reduce { acc, node ->
            add(acc, node)
        }.magnitude()
        println("Stage1: $stage1result")

        var max = 0
        nodes.indices.forEach { i ->
            nodes.indices.forEach { j ->
                val newNodes = lines.map { line ->
                    val iter = line.iterator()
                    iter.next()
                    parseNode(iter, 0)
                }
                if (i != j) {
                    val result = add(newNodes[i], newNodes[j])
                    val magnitude = result.magnitude()
                    max = max(max, magnitude)
                }
            }
        }
        println("Stage2: $max")
    }
    println("---- ${file.nameWithoutExtension} ---- Elapsed time: $timeInMillis")
}


