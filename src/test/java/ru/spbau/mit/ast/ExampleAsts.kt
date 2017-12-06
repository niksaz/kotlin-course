package ru.spbau.mit.ast

import ru.spbau.mit.ast.FunAst.*
import ru.spbau.mit.ast.FunAst.Operator.*

object ExampleAsts {
    val EXAMPLE_AST_1 = FunAst(File(
        Block(listOf(
            Variable(Identifier("a"), Number("10")),
            Variable(Identifier("b"), Number("20")),
            IfStatement(
                BinaryExpression(Identifier("a"), GT, Identifier("b")),
                Block(listOf(
                    FunctionCall(
                        Identifier("println"),
                        Arguments(listOf(Number("1")))))),
                Block(listOf(
                    FunctionCall(
                        Identifier("println"),
                        Arguments(listOf(Number("0"))))))))
        )
    ))

    val EXAMPLE_AST_2 = FunAst(File(
        Block(listOf(
            Function(
                Identifier("fib"),
                ParameterNames(listOf(Identifier("n"))),
                Block(listOf(
                    IfStatement(
                        BinaryExpression(Identifier("n"), LTE, Number("1")),
                        Block(listOf(
                            ReturnStatement(Number("1")))),
                        null),
                    ReturnStatement(
                        BinaryExpression(
                            FunctionCall(
                                Identifier("fib"),
                                Arguments(listOf(BinaryExpression(
                                    Identifier("n"), MINUS, Number("1"))))),
                            PLUS,
                            FunctionCall(
                                Identifier("fib"),
                                Arguments(listOf(BinaryExpression(
                                    Identifier("n"), MINUS, Number("2")))))))
                ))
            ),
            Variable(Identifier("i"), Number("1")),
            WhileBlock(
                BinaryExpression(Identifier("i"), LTE, Number("5")),
                Block(listOf(
                    FunctionCall(
                        Identifier("println"),
                        Arguments(listOf(
                            Identifier("i"),
                            FunctionCall(
                                Identifier("fib"),
                                Arguments(listOf(Identifier("i"))))))),
                    Assignment(
                        Identifier("i"),
                        BinaryExpression(Identifier("i"), PLUS, Number("1")))
                ))
            )
        ))
    ))

    val EXAMPLE_AST_3 = FunAst(File(
        Block(listOf(
            Function(
                Identifier("foo"),
                ParameterNames(listOf(Identifier("n"))),
                Block(listOf(
                    Function(
                        Identifier("bar"),
                        ParameterNames(listOf(Identifier("m"))),
                        Block(listOf(
                            ReturnStatement(
                                BinaryExpression(
                                    Identifier("m"), PLUS, Identifier("n")))))
                    ),
                    ReturnStatement(
                        FunctionCall(
                            Identifier("bar"),
                            Arguments(listOf(Number("1"))))
                    )
                ))
            ),
            FunctionCall(
                Identifier("println"),
                Arguments(listOf(
                    FunctionCall(
                        Identifier("foo"),
                        Arguments(listOf(Number("41"))))))
            )
        ))
    ))
}