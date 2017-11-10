package ru.spbau.mit.ast

/**
 * An intermediate AST representation which is built from Antlr's
 * [org.antlr.v4.runtime.tree.ParseTree].
 */
data class FunAst(val rootNode: FunAstNode) {
    interface FunAstNode

    data class File(
        val block: Block
    ) : FunAstNode

    data class Block(
        val statements: List<Statement>
    ) : FunAstNode

    data class BlockWithBraces(
        val block: Block
    ) : FunAstNode

    interface Statement : FunAstNode

    data class Function(
        val name: Identifier,
        val paramNames: ParameterNames,
        val body: BlockWithBraces
    ) : Statement

    data class Variable(
        val name: Identifier,
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
    ) : FunAstNode

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

    enum class Operator {
        MULTIPLY,
        DIVIDE,
        MODULUS,
        PLUS,
        MINUS,
        GT,
        LT,
        GTE,
        LTE,
        EQ,
        NQ,
        LOR,
        LAND
    }
}
