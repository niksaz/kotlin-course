package ru.spbau.mit.parser

import org.junit.Test
import ru.spbau.mit.ast.ExampleAsts
import kotlin.test.assertEquals

class FunParserTest {
    @Test
    fun parseExample1() {
        val ast = buildAstFrom("src/test/resources/example1.fun")
        val expectedAst = ExampleAsts.EXAMPLE_AST_1
        assertEquals(expectedAst, ast)
    }

    @Test
    fun parseExample2() {
        val ast = buildAstFrom("src/test/resources/example2.fun")
        val expectedAst = ExampleAsts.EXAMPLE_AST_2
        assertEquals(expectedAst, ast)
    }

    @Test
    fun parseExample3() {
        val ast = buildAstFrom("src/test/resources/example3.fun")
        val expectedAst = ExampleAsts.EXAMPLE_AST_3
        assertEquals(expectedAst, ast)
    }
}