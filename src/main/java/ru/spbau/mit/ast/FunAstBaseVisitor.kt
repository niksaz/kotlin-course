package ru.spbau.mit.ast

/** Base class for Visitors of [FunAst]. */
interface FunAstBaseVisitor<out T> {
    suspend fun visit(node: FunAst.Node): T {
        return node.accept(this)
    }

    suspend fun visitFile(file: FunAst.File): T

    suspend fun visitBlock(block: FunAst.Block): T

    suspend fun visitFunction(function: FunAst.Function): T

    suspend fun visitVariable(variable: FunAst.Variable): T

    suspend fun visitParameterNames(parameterNames: FunAst.ParameterNames): T

    suspend fun visitWhileBlock(whileBlock: FunAst.WhileBlock): T

    suspend fun visitIfStatement(ifStatement: FunAst.IfStatement): T

    suspend fun visitAssignment(assignment: FunAst.Assignment): T

    suspend fun visitReturnStatement(returnStatement: FunAst.ReturnStatement): T

    suspend fun visitFunctionCall(functionCall: FunAst.FunctionCall): T

    suspend fun visitArguments(arguments: FunAst.Arguments): T

    suspend fun visitBinaryExpression(binaryExpression: FunAst.BinaryExpression): T

    suspend fun visitIdentifier(identifier: FunAst.Identifier): T

    suspend fun visitNumber(number: FunAst.Number): T
}