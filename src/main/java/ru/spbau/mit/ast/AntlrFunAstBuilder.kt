package ru.spbau.mit.ast

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import ru.spbau.mit.parser.FunBaseVisitor
import ru.spbau.mit.parser.FunParser

/** Builds [FunAst] from [org.antlr.v4.runtime.tree.ParseTree]. */
class AntlrFunAstBuilder : FunBaseVisitor<FunAst.Node>() {
    fun buildAstFromContext(ctx: ParserRuleContext): FunAst {
        val rootNode = visit(ctx)
        return FunAst(rootNode)
    }

    override fun visitFile(ctx: FunParser.FileContext): FunAst.Node =
        FunAst.File(visitBlock(ctx.block()) as FunAst.Block)

    override fun visitBlock(ctx: FunParser.BlockContext): FunAst.Node {
        val statements = ctx.statement().map { visit(it) as FunAst.Statement }
        return FunAst.Block(statements.toList())
    }

    override fun visitFunction(ctx: FunParser.FunctionContext): FunAst.Node {
        val identifier = FunAst.Identifier(ctx.IDENTIFIER().text)
        val paramNames = visit(ctx.parameterNames()) as FunAst.ParameterNames
        val body = visit(ctx.blockWithBraces()) as FunAst.Block
        return FunAst.Function(identifier, paramNames, body)
    }

    override fun visitVariable(ctx: FunParser.VariableContext): FunAst.Node {
        val identifier = FunAst.Identifier(ctx.IDENTIFIER().text)
        val possibleExpression = ctx.expression()
        val expression = possibleExpression?.let { visit(it) as FunAst.Expression }
        return FunAst.Variable(identifier, expression)
    }

    override fun visitParameterNames(ctx: FunParser.ParameterNamesContext): FunAst.Node {
        val identifiers = ctx.IDENTIFIER()
        val params = identifiers?.map { FunAst.Identifier(it.text) }.orEmpty()
        return FunAst.ParameterNames(params)
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
        val possibleExpressions = ctx.expression()
        val expressions =
            possibleExpressions?.map { visit(it) as FunAst.Expression }?.toList() ?: listOf()
        return FunAst.Arguments(expressions)
    }

    private fun transformExpression(ctx: ParserRuleContext): FunAst.Expression {
        var left = visit(ctx.children[0]) as FunAst.Expression
        var i = 1
        while (i < ctx.childCount) {
            val opParseTree = ctx.children[i++]
            val operator = FunAst.Operator.getForSymbol(opParseTree.text)!!
            val right = visit(ctx.children[i++]) as FunAst.Expression
            left = FunAst.BinaryExpression(left, operator, right)
        }
        return left
    }

    override fun visitLorExpression(ctx: FunParser.LorExpressionContext): FunAst.Node
        = transformExpression(ctx)

    override fun visitLandExpression(ctx: FunParser.LandExpressionContext): FunAst.Node
        = transformExpression(ctx)

    override fun visitEquivalenceExpression(
        ctx: FunParser.EquivalenceExpressionContext
    ): FunAst.Node = transformExpression(ctx)

    override fun visitRelationalExpression(ctx: FunParser.RelationalExpressionContext): FunAst.Node
        = transformExpression(ctx)

    override fun visitAdditiveExpression(ctx: FunParser.AdditiveExpressionContext): FunAst.Node
        = transformExpression(ctx)

    override fun visitMultiplicativeExpression(
        ctx: FunParser.MultiplicativeExpressionContext
    ): FunAst.Node = transformExpression(ctx)

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

    override fun aggregateResult(aggregate: FunAst.Node?, nextResult: FunAst.Node?): FunAst.Node? =
        aggregate ?: nextResult
}