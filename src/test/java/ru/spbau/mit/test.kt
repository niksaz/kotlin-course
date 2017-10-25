package ru.spbau.mit

import kotlin.test.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun exampleTest1() {
        val n = 7
        val k = 2
        val isUniversity = booleanArrayOf(true, true, false, false, true, true, false)
        val graph = initGraph(n)
        addEdge(graph, 0, 2)
        addEdge(graph, 2, 1)
        addEdge(graph, 3, 4)
        addEdge(graph, 2, 6)
        addEdge(graph, 3, 2)
        addEdge(graph, 3, 5)
        val solver = Solver(k, isUniversity, graph)
        assertEquals(6, solver.solve())
    }

    @Test
    fun exampleTest2() {
        val n = 9
        val k = 3
        val isUniversity = booleanArrayOf(true, true, true, false, true, true, false, false, true)
        val graph = initGraph(n)
        addEdge(graph, 7, 8)
        addEdge(graph, 2, 1)
        addEdge(graph, 1, 6)
        addEdge(graph, 2, 3)
        addEdge(graph, 6, 5)
        addEdge(graph, 3, 4)
        addEdge(graph, 1, 0)
        addEdge(graph, 1, 7)
        val solver = Solver(k, isUniversity, graph)
        assertEquals(9, solver.solve())
    }

    private fun initGraph(size: Int): List<Vertex> {
        val graph = mutableListOf<Vertex>()
        for (i in 0 until size) {
            graph.add(Vertex())
        }
        return graph.toList()
    }

    private fun addEdge(graph: List<Vertex>, edgeA: Int, edgeB: Int) {
        graph[edgeA].edges.add(edgeB)
        graph[edgeB].edges.add(edgeA)
    }
}
