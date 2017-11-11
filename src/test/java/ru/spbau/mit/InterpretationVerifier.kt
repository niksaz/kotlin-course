package ru.spbau.mit

import com.google.common.truth.Truth.assertThat
import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.interpreter.FunInterpreter
import ru.spbau.mit.interpreter.FunInterpreter.InterpretationResult
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class InterpretationVerifier(
    private val expectedResult: InterpretationResult,
    private vararg val printedLines: String
) {
    private val byteOutputStream = ByteArrayOutputStream()
    private val printStream = PrintStream(byteOutputStream, AUTO_FLUSH_ENABLED)

    fun verifySourceFileInterpretation(sourceCodePath: String) {
        val result = interpretSourceFile(sourceCodePath, printStream)
        verifyInterpretation(result)
    }

    fun verifyAstInterpretation(ast: FunAst) {
        val funInterpreter = FunInterpreter(printStream)
        val result = funInterpreter.interpretAst(ast)
        verifyInterpretation(result)
    }

    private fun verifyInterpretation(result: InterpretationResult) {
        val outputBytes = byteOutputStream.toByteArray()
        val expectedOutputBytes = getBytesIfPrinted(printedLines)
        assertEquals(expectedResult, result)
        assertThat(outputBytes).isEqualTo(expectedOutputBytes)
    }

    private fun getBytesIfPrinted(lines: Array<out String>): ByteArray {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream, AUTO_FLUSH_ENABLED)
        for (arg in lines) {
            printStream.println(arg)
        }
        return byteOutputStream.toByteArray()
    }

    companion object {
        private val AUTO_FLUSH_ENABLED = true
    }
}