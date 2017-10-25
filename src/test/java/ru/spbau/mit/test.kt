package ru.spbau.mit

import kotlin.test.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun exampleTest1() {
        val vertexNumber = 7
        val universityPairs = 2
        val graph = List(vertexNumber, { Vertex() })
        graph[0].hasUniversity = true
        graph[1].hasUniversity = true
        graph[4].hasUniversity = true
        graph[5].hasUniversity = true
        addEdge(graph, 0, 2)
        addEdge(graph, 2, 1)
        addEdge(graph, 3, 4)
        addEdge(graph, 2, 6)
        addEdge(graph, 3, 2)
        addEdge(graph, 3, 5)
        val solver = Solver(universityPairs, graph)
        assertEquals(6, solver.solve())
    }

    @Test
    fun exampleTest2() {
        val vertexNumber = 9
        val universityPairs = 3
        val graph = List(vertexNumber, { Vertex() })
        graph[0].hasUniversity = true
        graph[1].hasUniversity = true
        graph[2].hasUniversity = true
        graph[4].hasUniversity = true
        graph[5].hasUniversity = true
        graph[8].hasUniversity = true
        addEdge(graph, 7, 8)
        addEdge(graph, 2, 1)
        addEdge(graph, 1, 6)
        addEdge(graph, 2, 3)
        addEdge(graph, 6, 5)
        addEdge(graph, 3, 4)
        addEdge(graph, 1, 0)
        addEdge(graph, 1, 7)
        val solver = Solver(universityPairs, graph)
        assertEquals(9, solver.solve())
    }

    private fun addEdge(graph: List<Vertex>, edgeA: Int, edgeB: Int) {
        graph[edgeA].edges.add(graph[edgeB])
        graph[edgeB].edges.add(graph[edgeA])
    }
}
