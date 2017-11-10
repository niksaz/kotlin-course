package ru.spbau.mit.ast

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import ru.spbau.mit.parser.FunBaseVisitor
import ru.spbau.mit.parser.FunParser

class FunAstBuilder : FunBaseVisitor<FunAst.Node>() {
    fun buildAstFromContext(ctx: ParserRuleContext): FunAst {
        val rootNode = visit(ctx)
        return FunAst(rootNode)
    }

    override fun visitFile(ctx: FunParser.FileContext): FunAst.Node {
        return FunAst.File(visitBlock(ctx.block()) as FunAst.Block)
    }

    override fun visitBlock(ctx: FunParser.BlockContext): FunAst.Node {
        val statements = ctx.statement().map { visit(it) as FunAst.Statement }
        return FunAst.Block(statements.toList())
    }

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext): FunAst.Node {
        return visit(ctx.block())
    }

    override fun visitStatement(ctx: FunParser.StatementContext): FunAst.Node {
        val possibleStatements = listOf<ParseTree?>(
            ctx.assignment(),
            ctx.expression(),
            ctx.function(),
            ctx.ifStatement(),
            ctx.returnStatement(),
            ctx.variable(),
            ctx.whileBlock()
        )
        val statement = possibleStatements.find { it != null }
        return visit(statement!!)
    }

    override fun visitFunction(ctx: FunParser.FunctionContext): FunAst.Node {
        val identifier = FunAst.Identifier(ctx.IDENTIFIER().text)
        val paramNames = visit(ctx.parameterNames()) as FunAst.ParameterNames
        val body = visit(ctx.blockWithBraces()) as FunAst.Block
        return FunAst.Function(identifier, paramNames, body)
    }

    override fun visitVariable(ctx: FunParser.VariableContext): FunAst.Node {
        val identifier = FunAst.Identifier(ctx.IDENTIFIER().text)
        val expression = visit(ctx.expression()) as FunAst.Expression
        return FunAst.Variable(identifier, expression)
    }

    override fun visitParameterNames(ctx: FunParser.ParameterNamesContext): FunAst.Node {
        val params = ctx.IDENTIFIER().map { FunAst.Identifier(it.text) }
        return FunAst.ParameterNames(params.toList())
    }

    override fun visitWhileBlock(ctx: FunParser.WhileBlockContext): FunAst.Node {
        val condition = visit(ctx.expression()) as FunAst.Expression
        val body = visit(ctx.blockWithBraces()) as FunAst.Block
        return FunAst.WhileBlock(condition, body)
    }

    override fun visitIfStatement(ctx: FunParser.IfStatementContext): FunAst.Node {
        val condition = visit(ctx.expression()) as FunAst.Expression
        val blocks = ctx.blockWithBraces().map { visit(it) }
        val body = blocks[0] as FunAst.Block
        val elseBody = blocks.getOrNull(1) as FunAst.Block?
        return FunAst.IfStatement(condition, body, elseBody)
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext): FunAst.Node {
        val identifier = FunAst.Identifier(ctx.IDENTIFIER().text)
        val expression = visit(ctx.expression()) as FunAst.Expression
        return FunAst.Assignment(identifier, expression)
    }

    override fun visitReturnStatement(ctx: FunParser.ReturnStatementContext): FunAst.Node {
        val expression = visit(ctx.expression()) as FunAst.Expression
        return FunAst.ReturnStatement(expression)
    }

    override fun visitFunctionCall(ctx: FunParser.FunctionCallContext): FunAst.Node {
        val identifier = FunAst.Identifier(ctx.IDENTIFIER().text)
        val arguments = visit(ctx.arguments()) as FunAst.Arguments
        return FunAst.FunctionCall(identifier, arguments)
    }

    override fun visitArguments(ctx: FunParser.ArgumentsContext): FunAst.Node {
        val expressions = ctx.expression().map { visit(it) as FunAst.Expression }
        return FunAst.Arguments(expressions)
    }

    override fun visitExpression(ctx: FunParser.ExpressionContext): FunAst.Node {
        val possibleExpressions = listOf<ParseTree?>(
            ctx.atomicExpression(),
            ctx.binaryExpression()
        )
        val expression = possibleExpressions.find { it != null }
        return visit(expression!!)
    }

    override fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext): FunAst.Node {
        val leftExpression = visit(ctx.atomicExpression()) as FunAst.Expression
        val operator = FunAst.Operator.getForSymbol(ctx.op.text)!!
        val rightExpression = visit(ctx.expression()) as FunAst.Expression
        return FunAst.BinaryExpression(leftExpression, operator, rightExpression)
    }

    override fun visitAtomicExpression(ctx: FunParser.AtomicExpressionContext): FunAst.Node {
        val identifier = ctx.IDENTIFIER()
        if (identifier != null) {
            return FunAst.Identifier(identifier.text)
        }
        val number = ctx.NUMBER()
        if (number != null) {
            return FunAst.Number(number.text)
        }
        val otherAtomicExpressions = listOf<ParseTree?>(
            ctx.functionCall(),
            ctx.expression()
        )
        val atomicExpression = otherAtomicExpressions.find { it != null }
        return visit(atomicExpression!!)
    }
}