package ru.spbau.mit.ast

import ru.spbau.mit.ast.FunAst.*
import ru.spbau.mit.ast.FunAst.Operator.*

object ExampleAsts {
    val EXAMPLE_AST_1 = FunAst(File(
        Block(listOf(
            Variable(Identifier("a", 1), Number("10", 1), 1),
            Variable(Identifier("b", 2), Number("20", 2), 2),
            IfStatement(
                BinaryExpression(Identifier("a", 3), GT, Identifier("b", 3), 3),
                Block(listOf(
                    FunctionCall(
                        Identifier("println", 4),
                        Arguments(listOf(Number("1", 4)), 4), 4)),
                    4),
                Block(listOf(
                    FunctionCall(
                        Identifier("println", 6),
                        Arguments(listOf(Number("0", 6)), 6), 6)),
                    6),
                3)),
            1),
        1))

    val EXAMPLE_AST_2 = FunAst(File(
        Block(listOf(
            Function(
                Identifier("fib", 1),
                ParameterNames(listOf(Identifier("n", 1)), 1),
                Block(listOf(
                    IfStatement(
                        BinaryExpression(Identifier("n", 2), LTE, Number("1", 2),2),
                        Block(listOf(
                            ReturnStatement(Number("1", 3), 3)),
                            3),
                        null,
                        2),
                    ReturnStatement(
                        BinaryExpression(
                            FunctionCall(
                                Identifier("fib", 5),
                                Arguments(listOf(BinaryExpression(
                                    Identifier("n", 5), MINUS, Number("1", 5), 5)),
                                    5),
                                5),
                            PLUS,
                            FunctionCall(
                                Identifier("fib", 5),
                                Arguments(listOf(BinaryExpression(
                                    Identifier("n", 5), MINUS, Number("2", 5), 5)),
                                    5),
                                5),
                            5),
                        5)),
                    2),
                1),
            Variable(Identifier("i", 8), Number("1", 8), 8),
            WhileBlock(
                BinaryExpression(Identifier("i", 9), LTE, Number("5", 9), 9),
                Block(listOf(
                    FunctionCall(
                        Identifier("println", 10),
                        Arguments(listOf(
                            Identifier("i", 10),
                            FunctionCall(
                                Identifier("fib", 10),
                                Arguments(listOf(Identifier("i", 10)), 10),
                                10)),
                            10),
                        10),
                    Assignment(
                        Identifier("i", 11),
                        BinaryExpression(Identifier("i", 11), PLUS, Number("1", 11), 11),
                        11)),
                    10),
                9)),
            1),
        1))

    val EXAMPLE_AST_3 = FunAst(File(
        Block(listOf(
            Function(
                Identifier("foo", 1),
                ParameterNames(listOf(Identifier("n", 1)), 1),
                Block(listOf(
                    Function(
                        Identifier("bar", 2),
                        ParameterNames(listOf(Identifier("m", 2)), 2),
                        Block(listOf(
                            ReturnStatement(
                                BinaryExpression(
                                    Identifier("m", 3), PLUS, Identifier("n", 3),
                                    3),
                                3)),
                            3),
                        2),
                    ReturnStatement(
                        FunctionCall(
                            Identifier("bar", 6),
                            Arguments(listOf(Number("1", 6)), 6),
                            6),
                        6)),
                    2),
                1),
            FunctionCall(
                Identifier("println", 9),
                Arguments(listOf(
                    FunctionCall(
                        Identifier("foo", 9),
                        Arguments(listOf(Number("41", 9)), 9),
                        9)),
                    9),
                9)),
            1),
        1))
}