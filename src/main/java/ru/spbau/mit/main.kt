package ru.spbau.mit

import java.util.*

data class Treelandia(private val nodes: List<Node>, private val universityPairs: Int) {
    private val subtreeUniversities = IntArray(nodes.size)

    private fun computeSubtreeUniversities(node: Node, parent: Node = node) {
        subtreeUniversities[node.index] = (if (node.hasUniversity) 1 else 0) +
            node.neighbourIndexes.map { nodes[it] }
                .filter { it !== parent }
                .map {
                    computeSubtreeUniversities(it, node)
                    subtreeUniversities[it.index]
                }
                .sum()
    }

    private fun findOptimalNode(node: Node, parent: Node = node): Node {
        val maxSubtreeChild = node.neighbourIndexes.map { nodes[it] }
            .filter { it != parent }
            .map { it to subtreeUniversities[it.index] }
            .maxBy { it.second }!!
        return if (maxSubtreeChild.second <= universityPairs) node else {
            findOptimalNode(maxSubtreeChild.first, node)
        }
    }

    private fun findDistancesToUniversities(
        node: Node,
        parent: Node = node,
        distanceToRoot: Long = 0L
    ): Long {
        return (if (node.hasUniversity) distanceToRoot else 0L) +
            node.neighbourIndexes.map { nodes[it] }
                .filter { it !== parent }
                .map { findDistancesToUniversities(it, node, distanceToRoot + 1L) }
                .sum()
    }

    fun computeMinSumDistances(): Long {
        computeSubtreeUniversities(nodes[0])
        val optimalNode = findOptimalNode(nodes[0])
        return findDistancesToUniversities(optimalNode)
    }
}

data class Node(val index: Int, val hasUniversity: Boolean, val neighbourIndexes: List<Int>)

fun readTreelandia(scanner: Scanner): Treelandia {
    val nodeCount = scanner.nextInt()
    val universityPairs = scanner.nextInt()
    val hasUniversityIndexes = BooleanArray(nodeCount)
    repeat(2 * universityPairs) {
        val universityIndex = scanner.nextInt() - 1
        hasUniversityIndexes[universityIndex] = true
    }
    val neighbourIndexes = Array<MutableList<Int>>(nodeCount) { mutableListOf() }
    repeat(nodeCount - 1) {
        val indexA = scanner.nextInt() - 1
        val indexB = scanner.nextInt() - 1
        neighbourIndexes[indexA].add(indexB)
        neighbourIndexes[indexB].add(indexA)
    }
    val nodes = List(nodeCount) { Node(it, hasUniversityIndexes[it], neighbourIndexes[it]) }
    return Treelandia(nodes, universityPairs)
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val treelandia = readTreelandia(scanner)
    println(treelandia.computeMinSumDistances())
}