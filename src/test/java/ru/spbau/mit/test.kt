package ru.spbau.mit

import org.junit.Test
import kotlin.test.assertEquals

class TestSource {
    @Test
    fun exampleTest1() {
        val nodeCount = 7
        val universityPairs = 2
        val hasUniversityIndexes = booleanArrayOf(true, true, false, false, true, true, false)
        val nodes = (0 until nodeCount).map { Node(it, hasUniversityIndexes[it]) }
        addEdge(nodes, 0, 2)
        addEdge(nodes, 2, 1)
        addEdge(nodes, 3, 4)
        addEdge(nodes, 2, 6)
        addEdge(nodes, 3, 2)
        addEdge(nodes, 3, 5)
        val sum = findMinDistancesSum(Treelandia(nodes, universityPairs))
        assertEquals(6L, sum)
    }

    @Test
    fun exampleTest2() {
        val nodeCount = 9
        val universityPairs = 3
        val hasUniversityIndexes = booleanArrayOf(
            true, true, true, false, true, true, false, false, true
        )
        val nodes = (0 until nodeCount).map { Node(it, hasUniversityIndexes[it]) }
        addEdge(nodes, 7, 8)
        addEdge(nodes, 2, 1)
        addEdge(nodes, 1, 6)
        addEdge(nodes, 2, 3)
        addEdge(nodes, 6, 5)
        addEdge(nodes, 3, 4)
        addEdge(nodes, 1, 0)
        addEdge(nodes, 1, 7)
        val sum = findMinDistancesSum(Treelandia(nodes, universityPairs))
        assertEquals(9L, sum)
    }

    private fun addEdge(graph: List<Node>, edgeA: Int, edgeB: Int) {
        graph[edgeA].edges.add(graph[edgeB])
        graph[edgeB].edges.add(graph[edgeA])
    }
}