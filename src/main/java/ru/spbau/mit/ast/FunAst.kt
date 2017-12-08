package ru.spbau.mit.ast

/**
 * An intermediate AST representation which is built from [org.antlr.v4.runtime.tree.ParseTree].
 */
data class FunAst(val rootNode: Node) {
     interface Node {
        val lineNumber: Int

        suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T
     }

    data class File(val block: Block, override val lineNumber: Int) : Node {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T = visitor.visitFile(this)
    }

    data class Block(val statements: List<Statement>, override val lineNumber: Int) : Node {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T = visitor.visitBlock(this)
    }

    interface Statement : Node

    data class Function(
        val identifier: Identifier,
        val paramNames: ParameterNames,
        val body: Block,
        override val lineNumber: Int
    ) : Statement {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitFunction(this)
    }

    data class Variable(
        val identifier: Identifier,
        val expression: Expression?,
        override val lineNumber: Int
    ) : Statement {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitVariable(this)
    }

    data class ParameterNames(
        val params: List<Identifier>,
        override val lineNumber: Int
    ) : Statement {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitParameterNames(this)
    }

    data class WhileBlock(
        val condition: Expression,
        val body: Block,
        override val lineNumber: Int
    ) : Statement {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitWhileBlock(this)
    }

    data class IfStatement(
        val condition: Expression,
        val body: Block,
        val elseBody: Block?,
        override val lineNumber: Int
    ) : Statement {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitIfStatement(this)
    }

    data class Assignment(
        val identifier: Identifier,
        val expression: Expression,
        override val lineNumber: Int
    ) : Statement {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitAssignment(this)
    }

    data class ReturnStatement(
        val expression: Expression,
        override val lineNumber: Int
    ) : Statement {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitReturnStatement(this)
    }

    interface Expression : Statement

    data class FunctionCall(
        val identifier: Identifier,
        val arguments: Arguments,
        override val lineNumber: Int
    ) : Expression {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitFunctionCall(this)
    }

    data class Arguments(val expressions: List<Expression>, override val lineNumber: Int) : Node {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitArguments(this)
    }

    data class BinaryExpression(
        val leftExpression: Expression,
        val operator: Operator,
        val rightExpression: Expression,
        override val lineNumber: Int
    ) : Expression {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitBinaryExpression(this)
    }

    data class Identifier(val name: String, override val lineNumber: Int) : Expression {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitIdentifier(this)
    }

    data class Number(val literal: String, override val lineNumber: Int) : Expression {
        override suspend fun <T> accept(visitor: FunAstBaseVisitor<T>): T =
            visitor.visitNumber(this)
    }

    enum class Operator(val symbol: String) {
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
            fun getForSymbol(symbol: String): Operator? =
                values().firstOrNull { it.symbol == symbol }
        }
    }
}