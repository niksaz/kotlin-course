package ru.spbau.mit.interpreter

import ru.spbau.mit.parser.FunBaseVisitor
import ru.spbau.mit.parser.FunParser

class FunPrinterVisitor : FunBaseVisitor<Unit>() {
    override fun visitFile(ctx: FunParser.FileContext) {
        println("File:")
        super.visitFile(ctx)
    }

    override fun visitBlock(ctx: FunParser.BlockContext) {
        println("Block:")
        super.visitBlock(ctx)
    }

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext) {
        println("BlockWithBraces:")
        super.visitBlockWithBraces(ctx)
    }

    override fun visitStatement(ctx: FunParser.StatementContext) {
        println("Statement:")
        super.visitStatement(ctx)
    }

    override fun visitFunction(ctx: FunParser.FunctionContext) {
        println("Function:")
        super.visitFunction(ctx)
    }

    override fun visitVariable(ctx: FunParser.VariableContext) {
        println("Variable: " + ctx.text)
        super.visitVariable(ctx)
    }

    override fun visitParameterNames(ctx: FunParser.ParameterNamesContext) {
        print("ParameterNames:" + ctx.text)
        super.visitParameterNames(ctx)
    }

    override fun visitWhileBlock(ctx: FunParser.WhileBlockContext) {
        println("WhileBlock:")
        super.visitWhileBlock(ctx)
    }

    override fun visitIfStatement(ctx: FunParser.IfStatementContext) {
        println("IfStatement:")
        super.visitIfStatement(ctx)
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext) {
        println("Assignment: " + ctx.text)
        super.visitAssignment(ctx)
    }

    override fun visitReturnStatement(ctx: FunParser.ReturnStatementContext) {
        println("ReturnStatement: " + ctx.text)
        super.visitReturnStatement(ctx)
    }

    override fun visitFunctionCall(ctx: FunParser.FunctionCallContext) {
        println("FunctionCall: " + ctx.text)
        super.visitFunctionCall(ctx)
    }

    override fun visitArguments(ctx: FunParser.ArgumentsContext) {
        println("Arguments: " + ctx.text)
        super.visitArguments(ctx)
    }

    override fun visitExpression(ctx: FunParser.ExpressionContext) {
        println("Expression: " + ctx.text)
        super.visitExpression(ctx)
    }

    override fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext) {
        println("BinaryExpression: " + ctx.text)
        super.visitBinaryExpression(ctx)
    }

    override fun visitAtomExpression(ctx: FunParser.AtomExpressionContext) {
        println("AtomExpression: " + ctx.text)
        super.visitAtomExpression(ctx)
    }
}