package ru.spbau.mit.ast

/** Base class for Visitors of [FunAst]. */
interface FunAstBaseVisitor<out T> {
    fun visit(node: FunAst.Node): T {
        return node.accept(this)
    }

    fun visitFile(file: FunAst.File): T

    fun visitBlock(block: FunAst.Block): T

    fun visitFunction(function: FunAst.Function): T

    fun visitVariable(variable: FunAst.Variable): T

    fun visitParameterNames(parameterNames: FunAst.ParameterNames): T

    fun visitWhileBlock(whileBlock: FunAst.WhileBlock): T

    fun visitIfStatement(ifStatement: FunAst.IfStatement): T

    fun visitAssignment(assignment: FunAst.Assignment): T

    fun visitReturnStatement(returnStatement: FunAst.ReturnStatement): T

    fun visitFunctionCall(functionCall: FunAst.FunctionCall): T

    fun visitArguments(arguments: FunAst.Arguments): T

    fun visitBinaryExpression(binaryExpression: FunAst.BinaryExpression): T

    fun visitIdentifier(identifier: FunAst.Identifier): T

    fun visitNumber(number: FunAst.Number): T
}