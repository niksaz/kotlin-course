package ru.spbau.mit.interpreter

import org.junit.Test
import ru.spbau.mit.InterpretationVerifier
import ru.spbau.mit.ast.ExampleAsts
import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.ast.FunAst.*
import ru.spbau.mit.ast.FunAst.Function
import ru.spbau.mit.ast.FunAst.Number
import ru.spbau.mit.interpreter.FunInterpreter.Companion.UNIT_INTERPRETATION

class FunInterpreterTest {
    @Test
    fun interpretExample1() {
        val ast = ExampleAsts.EXAMPLE_AST_1
        InterpretationVerifier(UNIT_INTERPRETATION, "0").verifyAstInterpretation(ast)
    }

    @Test
    fun interpretExample2() {
        val ast = ExampleAsts.EXAMPLE_AST_2
        InterpretationVerifier(UNIT_INTERPRETATION, "1 1", "2 2", "3 3", "4 5", "5 8")
            .verifyAstInterpretation(ast)
    }

    @Test
    fun interpretExample3() {
        val ast = ExampleAsts.EXAMPLE_AST_3
        InterpretationVerifier(UNIT_INTERPRETATION, "42").verifyAstInterpretation(ast)
    }

    @Test(expected = FunInterpretationException::class)
    fun interpretUndefinedFunction() {
        val ast = FunAst(FunctionCall(
            Identifier("fib", 1),
            Arguments(listOf(Number("5", 1)), 1),
            1))
        InterpretationVerifier(UNIT_INTERPRETATION).verifyAstInterpretation(ast)
    }

    @Test(expected = FunInterpretationException::class)
    fun interpretOverloadedFunctions() {
        val ast = FunAst(Block(listOf(
            Function(
                Identifier("print", 1),
                ParameterNames(listOf(), 1),
                Block(listOf(), 1),
                1),
            Function(
                Identifier("print", 1),
                ParameterNames(listOf(Identifier("s", 1)), 1),
                Block(listOf(), 1),
                1)),
            1))
        InterpretationVerifier(UNIT_INTERPRETATION).verifyAstInterpretation(ast)
    }
}