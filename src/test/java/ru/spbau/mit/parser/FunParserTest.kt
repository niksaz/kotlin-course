package ru.spbau.mit.parser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Test
import ru.spbau.mit.ast.ExampleAsts
import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.ast.FunAstBuilder
import kotlin.test.assertEquals

class FunParserTest {
    @Test
    fun parseExample1() {
        val ast = getFunAstFrom("src/test/resources/example1.fun")
        val expectedAst = ExampleAsts.EXAMPLE_AST_1
        assertEquals(expectedAst, ast)
    }

    @Test
    fun parseExample2() {
        val ast = getFunAstFrom("src/test/resources/example2.fun")
        val expectedAst = ExampleAsts.EXAMPLE_AST_2
        assertEquals(expectedAst, ast)
    }

    @Test
    fun parseExample3() {
        val ast = getFunAstFrom("src/test/resources/example3.fun")
        val expectedAst = ExampleAsts.EXAMPLE_AST_3
        assertEquals(expectedAst, ast)
    }

    companion object {
        private fun getFunAstFrom(sourceFilePath: String): FunAst {
            val funLexer = FunLexer(CharStreams.fromFileName(sourceFilePath))
            val tokens = CommonTokenStream(funLexer)
            val parser = FunParser(tokens)
            return FunAstBuilder().buildAstFromContext(parser.file())
        }
    }
}