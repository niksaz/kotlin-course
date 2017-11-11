package ru.spbau.mit.interpreter

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import ru.spbau.mit.ast.ExampleAsts
import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.ast.FunAst.*
import ru.spbau.mit.ast.FunAst.Function
import ru.spbau.mit.ast.FunAst.Number
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class FunInterpreterTest {
    @Test
    fun interpretExample1() {
        val ast = ExampleAsts.EXAMPLE_AST_1
        val (interpretationResult, outputBytes) = interpret(ast)
        val expectedInterpretationResult = FunInterpreter.UNIT_INTERPRETATION
        val expectedOutputBytes = getBytesIfPrinted("0")
        assertEquals(expectedInterpretationResult, interpretationResult)
        assertThat(outputBytes).isEqualTo(expectedOutputBytes)
    }

    @Test
    fun interpretExample2() {
        val ast = ExampleAsts.EXAMPLE_AST_2
        val (interpretationResult, outputBytes) = interpret(ast)
        val expectedInterpretationResult = FunInterpreter.UNIT_INTERPRETATION
        val expectedOutputBytes = getBytesIfPrinted("1 1", "2 2", "3 3", "4 5", "5 8")
        assertEquals(expectedInterpretationResult, interpretationResult)
        assertThat(outputBytes).isEqualTo(expectedOutputBytes)
    }

    @Test
    fun interpretExample3() {
        val ast = ExampleAsts.EXAMPLE_AST_3
        val (interpretationResult, outputBytes) = interpret(ast)
        val expectedInterpretationResult = FunInterpreter.UNIT_INTERPRETATION
        val expectedOutputBytes = getBytesIfPrinted("42")
        assertEquals(expectedInterpretationResult, interpretationResult)
        assertThat(outputBytes).isEqualTo(expectedOutputBytes)
    }

    @Test(expected = FunInterpretationException::class)
    fun interpretUndefinedFunction() {
        val ast = FunAst(FunctionCall(
            Identifier("fib"),
            Arguments(listOf(Number("5")))
        ))
        interpret(ast)
    }

    @Test(expected = FunInterpretationException::class)
    fun interpretOverloadedFunctions() {
        val ast = FunAst(Block(listOf(
            Function(Identifier("print"), ParameterNames(listOf()), Block(listOf())),
            Function(
                Identifier("print"),
                ParameterNames(listOf(Identifier("s"))),
                Block(listOf())))
        ))
        interpret(ast)
    }

    companion object {
        private val AUTO_FLUSH_ENABLED = true

        private fun interpret(ast: FunAst): Pair<FunInterpreter.InterpretationResult, ByteArray> {
            val byteOutputStream = ByteArrayOutputStream()
            val printStream = PrintStream(byteOutputStream, AUTO_FLUSH_ENABLED)
            val funInterpreter = FunInterpreter(printStream)
            return Pair(funInterpreter.interpretAst(ast), byteOutputStream.toByteArray())
        }

        private fun getBytesIfPrinted(vararg args: String): ByteArray {
            val byteOutputStream = ByteArrayOutputStream()
            val printStream = PrintStream(byteOutputStream, AUTO_FLUSH_ENABLED)
            for (arg in args) {
                printStream.println(arg)
            }
            return byteOutputStream.toByteArray()
        }
    }
}