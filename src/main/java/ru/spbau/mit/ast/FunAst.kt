package ru.spbau.mit.ast

/**
 * An intermediate AST representation which is built from Antlr's
 * [org.antlr.v4.runtime.tree.ParseTree].
 */
data class FunAst(val rootNode: Node) {
    interface Node

    data class File(
        val block: Block
    ) : Node

    data class Block(
        val statements: List<Statement>
    ) : Node

    data class BlockWithBraces(
        val block: Block
    ) : Node

    interface Statement : Node

    data class Function(
        val identifier: Identifier,
        val paramNames: ParameterNames,
        val body: BlockWithBraces
    ) : Statement

    data class Variable(
        val identifier: Identifier,
        val expression: Expression
    ) : Statement

    data class ParameterNames(
        val params: List<Identifier>
    ) : Statement

    data class WhileBlock(
        val condition: Expression,
        val body: BlockWithBraces
    ) : Statement

    data class IfStatement(
        val condition: Expression,
        val body: BlockWithBraces,
        val elseBody: BlockWithBraces?
    ) : Statement

    data class Assignment(
        val identifier: Identifier,
        val expression: Expression
    ) : Statement

    data class ReturnStatement(
        val expression: Expression
    ) : Statement

    data class FunctionCall(
        val identifier: Identifier,
        val arguments: Arguments
    ) : Expression

    data class Arguments(
        val expressions: List<Expression>
    ) : Node

    interface Expression : Statement

    data class BinaryExpression(
        val leftExpression: Expression,
        val operator: Operator,
        val rightExpression: Expression
    ) : Expression

    data class Identifier(
        val name: String
    ) : Expression

    data class Number(
        val literal: String
    ) : Expression

    enum class Operator(private val symbol: String) {
        MULTIPLY("*"),
        DIVIDE("/"),
        MODULUS("%"),
        PLUS("+"),
        MINUS("-"),
        GT(">"),
        LT("<"),
        GTE(">="),
        LTE("<="),
        EQ("=="),
        NQ("!="),
        LOR("||"),
        LAND("&&");

        companion object {
            fun getForSymbol(symbol: String): Operator? {
                return values().firstOrNull { it.symbol == symbol }
            }
        }
    }
}
