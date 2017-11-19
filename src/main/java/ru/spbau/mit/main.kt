package ru.spbau.mit

import java.io.*
import java.util.*

class BufferedScanner(stream: InputStream) {
    private val reader: BufferedReader = BufferedReader(InputStreamReader(stream), 32768)
    private var tokenizer: StringTokenizer? = null

    private fun next(): String {
        while (tokenizer == null || !tokenizer!!.hasMoreTokens()) {
            tokenizer = StringTokenizer(reader.readLine())
        }
        return tokenizer!!.nextToken()
    }

    fun nextInt() = Integer.parseInt(next())
}

data class Treelandia(val nodes: List<Node>, val universityPairs: Int) {
    val vertexes: Int
        get() = nodes.size
}

data class Node(
    val index: Int,
    val hasUniversity: Boolean,
    val edges: MutableList<Node> = mutableListOf()
)

fun readTreelandia(scanner: BufferedScanner): Treelandia {
    val vertexNumber = scanner.nextInt()
    val universityPairs = scanner.nextInt()
    val hasUniversityIndexes = BooleanArray(vertexNumber)
    repeat(2 * universityPairs) {
        val universityIndex = scanner.nextInt() - 1
        hasUniversityIndexes[universityIndex] = true
    }
    val nodes = (0 until vertexNumber).map { Node(it, hasUniversityIndexes[it]) }
    repeat(vertexNumber - 1) {
        val edgeA = scanner.nextInt() - 1
        val edgeB = scanner.nextInt() - 1
        nodes[edgeA].edges.add(nodes[edgeB])
        nodes[edgeB].edges.add(nodes[edgeA])
    }
    return Treelandia(nodes, universityPairs)
}

fun computeSubtreeUniversities(subtreeUniversities: IntArray, node: Node, parent: Node = node) {
    subtreeUniversities[node.index] = (if (node.hasUniversity) 1 else 0) +
        node.edges
            .filter { it !== parent }
            .map {
                computeSubtreeUniversities(subtreeUniversities, it, node)
                subtreeUniversities[it.index]
            }
            .sum()
}

fun findOptimalVertex(
    subtreeUniversities: IntArray,
    universityPairs: Int,
    node: Node,
    parent: Node = node
): Node {
    val maxSubtreeChild = node.edges
        .filter { it != parent }
        .map { it to subtreeUniversities[it.index] }
        .maxBy { it.second }!!
    return if (maxSubtreeChild.second <= universityPairs) node else {
        findOptimalVertex(subtreeUniversities, universityPairs, maxSubtreeChild.first, node)
    }
}

fun findDistancesToUniversities(node: Node, parent: Node = node, distanceToRoot: Long = 0L): Long {
    return (if (node.hasUniversity) distanceToRoot else 0L) +
        node.edges
            .filter { it !== parent }
            .map { findDistancesToUniversities(it, node, distanceToRoot + 1L) }
            .sum()
}

fun findMinDistancesSum(treelandia: Treelandia): Long {
    val subtreeUniversities = IntArray(treelandia.vertexes)
    computeSubtreeUniversities(subtreeUniversities, treelandia.nodes[0])
    val optimalVertex =
        findOptimalVertex(subtreeUniversities, treelandia.universityPairs, treelandia.nodes[0])
    return findDistancesToUniversities(optimalVertex)
}

fun main(args: Array<String>) {
    val scanner = BufferedScanner(System.`in`)
    val minDistancesSum = findMinDistancesSum(readTreelandia(scanner))
    println(minDistancesSum)
}