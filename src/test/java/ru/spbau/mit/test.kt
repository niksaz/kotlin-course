package ru.spbau.mit

import org.junit.Test
import kotlin.test.assertEquals

class TestSource {
    @Test
    fun exampleTest1() {
        val nodeCount = 7
        val universityPairs = 2
        val hasUniversityIndexes = booleanArrayOf(true, true, false, false, true, true, false)
        val neighbourIndexes = Array<MutableList<Int>>(nodeCount) { mutableListOf() }
        addEdge(neighbourIndexes, 0, 2)
        addEdge(neighbourIndexes, 2, 1)
        addEdge(neighbourIndexes, 3, 4)
        addEdge(neighbourIndexes, 2, 6)
        addEdge(neighbourIndexes, 3, 2)
        addEdge(neighbourIndexes, 3, 5)
        val nodes = List(nodeCount) { Node(it, hasUniversityIndexes[it], neighbourIndexes[it]) }
        val sum = Treelandia(nodes, universityPairs).computeMinSumDistances()
        assertEquals(6L, sum)
    }

    @Test
    fun exampleTest2() {
        val nodeCount = 9
        val universityPairs = 3
        val hasUniversityIndexes = booleanArrayOf(
            true, true, true, false, true, true, false, false, true
        )
        val neighbourIndexes = Array<MutableList<Int>>(nodeCount) { mutableListOf() }
        addEdge(neighbourIndexes, 7, 8)
        addEdge(neighbourIndexes, 2, 1)
        addEdge(neighbourIndexes, 1, 6)
        addEdge(neighbourIndexes, 2, 3)
        addEdge(neighbourIndexes, 6, 5)
        addEdge(neighbourIndexes, 3, 4)
        addEdge(neighbourIndexes, 1, 0)
        addEdge(neighbourIndexes, 1, 7)
        val nodes = List(nodeCount) { Node(it, hasUniversityIndexes[it], neighbourIndexes[it]) }
        val sum = Treelandia(nodes, universityPairs).computeMinSumDistances()
        assertEquals(9L, sum)
    }

    private fun addEdge(graph: Array<MutableList<Int>>, edgeA: Int, edgeB: Int) {
        graph[edgeA].add(edgeB)
        graph[edgeB].add(edgeA)
    }
}