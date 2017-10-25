package ru.spbau.mit

import java.io.*
import java.util.*

class Solver(private val universityPairs: Int, private val graph: List<Vertex>) {
    fun solve(): Long {
        computeSubtreeUniversityCount(graph[0])
        val root = findOptimalVertex(graph[0])
        return sumFromRoot(root, root, 0)
    }

    private fun computeSubtreeUniversityCount(vertex: Vertex, parent: Vertex = vertex) {
        if (vertex.hasUniversity) {
            vertex.subtreeUniversityCount++
        }
        for (toVertex in vertex.edges) {
            if (toVertex !== parent) {
                computeSubtreeUniversityCount(toVertex, vertex)
                val childCount = toVertex.subtreeUniversityCount
                vertex.subtreeUniversityCount += childCount
                if (childCount > vertex.maxSubtreeUniversityCount?.first ?: 0) {
                    vertex.maxSubtreeUniversityCount = Pair(childCount, toVertex)
                }
            }
        }
    }

    private fun findOptimalVertex(vertex: Vertex): Vertex {
        return if (vertex.maxSubtreeUniversityCount!!.first > universityPairs) {
            findOptimalVertex(vertex.maxSubtreeUniversityCount!!.second)
        } else {
            vertex
        }
    }

    private fun sumFromRoot(vertex: Vertex, parent: Vertex, distanceToRoot: Int): Long {
        return (if (vertex.hasUniversity) distanceToRoot.toLong() else 0L) +
            vertex.edges
            .filter { it !== parent }
            .map { sumFromRoot(it, vertex, distanceToRoot + 1) }
            .sum()
    }
}

data class Vertex(
    val edges: MutableList<Vertex> = mutableListOf(),
    var hasUniversity: Boolean = false,
    var subtreeUniversityCount: Int = 0,
    var maxSubtreeUniversityCount: Pair<Int, Vertex>? = null)

class InputReader(stream: InputStream) {
    private val reader: BufferedReader = BufferedReader(InputStreamReader(stream), 32768)
    private var tokenizer: StringTokenizer? = null

    private fun next(): String {
        while (tokenizer == null || !tokenizer!!.hasMoreTokens()) {
            try {
                tokenizer = StringTokenizer(reader.readLine())
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        return tokenizer!!.nextToken()
    }

    fun nextInt(): Int {
        return Integer.parseInt(next())
    }
}

fun main(args: Array<String>) {
    val reader = InputReader(System.`in`)

    val vertexNumber = reader.nextInt()
    val graph = List(vertexNumber, { Vertex() })
    val universityPairs = reader.nextInt()
    for (i in 0 until 2 * universityPairs) {
        val universityIndex = reader.nextInt() - 1
        graph[universityIndex].hasUniversity = true
    }
    for (i in 0 until vertexNumber - 1) {
        val edgeA = reader.nextInt() - 1
        val edgeB = reader.nextInt() - 1
        graph[edgeA].edges.add(graph[edgeB])
        graph[edgeB].edges.add(graph[edgeA])
    }

    val solver = Solver(universityPairs, graph)
    println(solver.solve())
}
