package ru.spbau.mit

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.experimental.runBlocking
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
    private val printStream = PrintStream(byteOutputStream, true)

    fun verifySourceFileInterpretation(sourceCodePath: String) {
        val result = interpretSourceFile(sourceCodePath, printStream)
        verifyInterpretation(result)
    }

    fun verifyAstInterpretation(ast: FunAst) {
        val funInterpreter = FunInterpreter(printStream)
        val result = runBlocking { funInterpreter.interpretAst(ast)  }
        verifyInterpretation(result)
    }

    private fun verifyInterpretation(result: InterpretationResult) {
        val outputBytes = byteOutputStream.toByteArray()
        val expectedOutputBytes = getBytesIfPrinted(*printedLines)
        assertEquals(expectedResult, result)
        assertThat(outputBytes).isEqualTo(expectedOutputBytes)
    }
}