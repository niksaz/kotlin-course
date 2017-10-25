package ru.spbau.mit

import java.io.*
import java.util.*

class Solver(
    private val k: Int,
    private val universities: BooleanArray,
    private val graph: List<Vertex>) {

    private val subtreeUniversityCount = IntArray(universities.size)
    private val maxSubtreeUniversityCount = Array(universities.size, { Pair(0, 0) } )

    fun solve(): Long {
        computeSubtreeUniversityCount(0, 0)
        val root = findOptimalVertex(0)
        return sumFromRoot(root, root, 0)
    }

    private fun computeSubtreeUniversityCount(vertex: Int, parent: Int): Int {
        if (universities[vertex]) {
            subtreeUniversityCount[vertex]++
        }
        for (toVertex in graph[vertex].edges) {
            if (toVertex != parent) {
                val childCount = computeSubtreeUniversityCount(toVertex, vertex)
                subtreeUniversityCount[vertex] += childCount
                if (childCount > maxSubtreeUniversityCount[vertex].first) {
                    maxSubtreeUniversityCount[vertex] = Pair(childCount, toVertex)
                }
            }
        }
        return subtreeUniversityCount[vertex]
    }

    private fun findOptimalVertex(vertex: Int): Int {
        return if (maxSubtreeUniversityCount[vertex].first > k) {
            findOptimalVertex(maxSubtreeUniversityCount[vertex].second)
        } else {
            vertex
        }
    }

    private fun sumFromRoot(vertex: Int, parent: Int, distanceToRoot: Int): Long {
        return (if (universities[vertex]) distanceToRoot.toLong() else 0L) +
            graph[vertex].edges
            .filter { it != parent }
            .map { sumFromRoot(it, vertex, distanceToRoot + 1) }
            .sum()
    }
}

data class Vertex(val edges: MutableList<Int> = mutableListOf())

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

    val n = reader.nextInt()
    val graph = mutableListOf<Vertex>()
    for (i in 0 until n) {
        graph.add(Vertex())
    }
    val k = reader.nextInt()
    val isUniversity = BooleanArray(n)
    for (i in 0 until 2 * k) {
        val universityIndex = reader.nextInt() - 1
        isUniversity[universityIndex] = true
    }
    for (i in 0 until n - 1) {
        val edgeA = reader.nextInt() - 1
        val edgeB = reader.nextInt() - 1
        graph[edgeA].edges.add(edgeB)
        graph[edgeB].edges.add(edgeA)
    }

    val solver = Solver(k, isUniversity, graph.toList())
    println(solver.solve())
}
