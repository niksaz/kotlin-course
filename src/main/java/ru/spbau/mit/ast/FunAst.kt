package ru.spbau.mit.ast

/**
 * An intermediate AST representation which is built from [org.antlr.v4.runtime.tree.ParseTree].
 */
data class FunAst(val rootNode: Node) {
    interface Node {
        fun <T> accept(visitor: FunAstBaseVisitor<T>): T
    }

    data class File(
        val block: Block
    ) : Node {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitFile(this)
        }
    }

    data class Block(
        val statements: List<Statement>
    ) : Node {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitBlock(this)
        }
    }

    interface Statement : Node

    data class Function(
        val identifier: Identifier,
        val paramNames: ParameterNames,
        val body: Block
    ) : Statement {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitFunction(this)
        }
    }

    data class Variable(
        val identifier: Identifier,
        val expression: Expression?
    ) : Statement {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitVariable(this)
        }
    }

    data class ParameterNames(
        val params: List<Identifier>
    ) : Statement {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitParameterNames(this)
        }
    }

    data class WhileBlock(
        val condition: Expression,
        val body: Block
    ) : Statement {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitWhileBlock(this)
        }
    }

    data class IfStatement(
        val condition: Expression,
        val body: Block,
        val elseBody: Block?
    ) : Statement {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitIfStatement(this)
        }
    }

    data class Assignment(
        val identifier: Identifier,
        val expression: Expression
    ) : Statement {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitAssignment(this)
        }
    }

    data class ReturnStatement(
        val expression: Expression
    ) : Statement {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitReturnStatement(this)
        }
    }

    data class FunctionCall(
        val identifier: Identifier,
        val arguments: Arguments
    ) : Expression {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitFunctionCall(this)
        }
    }

    data class Arguments(
        val expressions: List<Expression>
    ) : Node {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitArguments(this)
        }
    }

    interface Expression : Statement

    data class BinaryExpression(
        val leftExpression: Expression,
        val operator: Operator,
        val rightExpression: Expression
    ) : Expression {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitBinaryExpression(this)
        }
    }

    data class Identifier(
        val name: String
    ) : Expression {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitIdentifier(this)
        }
    }

    data class Number(
        val literal: String
    ) : Expression {
        override fun <T> accept(visitor: FunAstBaseVisitor<T>): T {
            return visitor.visitNumber(this)
        }
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
            fun getForSymbol(symbol: String): Operator? {
                return values().firstOrNull { it.symbol == symbol }
            }
        }
    }
}