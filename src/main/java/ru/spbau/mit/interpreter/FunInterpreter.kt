package ru.spbau.mit.interpreter

import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.ast.FunAst.Operator
import ru.spbau.mit.ast.FunAstBaseVisitor
import ru.spbau.mit.interpreter.FunInterpreter.InterpretationResult
import java.io.PrintStream

/** A visitor for interpreting the [FunAst] nodes. */
open class FunInterpreter(
    protected val printStream: PrintStream,
    private val context: FunContext = FunContext(setOf(PRINTLN_FUN_NAME))
) : FunAstBaseVisitor<InterpretationResult> {
    suspend fun interpretAst(ast: FunAst): InterpretationResult = visit(ast.rootNode)

    suspend override fun visitFile(file: FunAst.File): InterpretationResult = visitBlock(file.block)

    override suspend fun visitBlock(block: FunAst.Block): InterpretationResult {
        context.enterScope()
        for (statement in block.statements) {
            val interpretedStatement = visit(statement)
            if (interpretedStatement.shouldReturn) {
                context.leaveScope()
                return interpretedStatement
            }
        }
        context.leaveScope()
        return UNIT_INTERPRETATION
    }

    override suspend fun visitFunction(function: FunAst.Function): InterpretationResult {
        context.declareFunction(function)
        return UNIT_INTERPRETATION
    }

    override suspend fun visitVariable(variable: FunAst.Variable): InterpretationResult {
        val expression = variable.expression
        if (expression != null) {
            val interpretedExpression = visit(expression)
            context.declareVariable(variable.identifier, interpretedExpression.value!!)
        } else {
            context.declareVariable(variable.identifier, null)
        }
        return UNIT_INTERPRETATION
    }

    override suspend fun visitParameterNames(
        parameterNames: FunAst.ParameterNames
    ): InterpretationResult = UNIT_INTERPRETATION

    override suspend fun visitWhileBlock(whileBlock: FunAst.WhileBlock): InterpretationResult {
        while (visit(whileBlock.condition).value!! != 0) {
            val interpretedBody = visit(whileBlock.body)
            if (interpretedBody.shouldReturn) {
                return interpretedBody
            }
        }
        return UNIT_INTERPRETATION
    }

    override suspend fun visitIfStatement(
        ifStatement: FunAst.IfStatement
    ): InterpretationResult = when {
        visit(ifStatement.condition).value!! != 0 -> visit(ifStatement.body)
        ifStatement.elseBody != null -> visit(ifStatement.elseBody)
        else -> UNIT_INTERPRETATION
    }

    override suspend fun visitAssignment(assignment: FunAst.Assignment): InterpretationResult {
        val scope = context.getVariable(assignment.identifier).second
        context.setVariable(scope, assignment.identifier, visit(assignment.expression).value!!)
        return UNIT_INTERPRETATION
    }

    override suspend fun visitReturnStatement(
        returnStatement: FunAst.ReturnStatement
    ): InterpretationResult = InterpretationResult(visit(returnStatement.expression).value, true)

    override suspend fun visitFunctionCall(
        functionCall: FunAst.FunctionCall
    ): InterpretationResult {
        val function = context.getFunction(
            functionCall.identifier, functionCall.arguments.expressions.size)
        val args = functionCall.arguments.expressions.map { visit(it).value!! }
        if (function == null) {
            return runBuiltInFunction(functionCall.identifier, args)
        }
        context.enterScope()
        val params = function.paramNames.params
        params.zip(args).forEach { (param, arg) ->
            context.declareVariable(param, arg)
        }
        val interpretedBody = visit(function.body)
        context.leaveScope()
        return InterpretationResult(
            if (interpretedBody.shouldReturn) interpretedBody.value!! else DEFAULT_FUNCTION_RESULT,
            false
        )
    }

    private fun runBuiltInFunction(
        identifier: FunAst.Identifier, args: List<Int>
    ): InterpretationResult {
        if (identifier.name == PRINTLN_FUN_NAME) {
            val argsString = args.joinToString(" ") { it.toString() }
            printStream.println(argsString)
            return InterpretationResult(DEFAULT_FUNCTION_RESULT, false)
        }
        throw FunInterpretationException("Unknown built-in function " + identifier.name + ".")
    }

    override suspend fun visitArguments(arguments: FunAst.Arguments): InterpretationResult =
        UNIT_INTERPRETATION

    override suspend fun visitBinaryExpression(
        binaryExpression: FunAst.BinaryExpression
    ): InterpretationResult {
        val leftValue = visit(binaryExpression.leftExpression).value!!
        val rightValue = visit(binaryExpression.rightExpression).value!!
        val op = binaryExpression.operator
        try {
            val resultValue = when (op) {
                Operator.MULTIPLY -> leftValue * rightValue
                Operator.DIVIDE -> leftValue / rightValue
                Operator.MODULUS -> leftValue % rightValue
                Operator.PLUS -> leftValue + rightValue
                Operator.MINUS -> leftValue - rightValue
                Operator.GT -> boolToInt(leftValue > rightValue)
                Operator.LT -> boolToInt(leftValue < rightValue)
                Operator.GTE -> boolToInt(leftValue >= rightValue)
                Operator.LTE -> boolToInt(leftValue <= rightValue)
                Operator.EQ -> boolToInt(leftValue == rightValue)
                Operator.NQ -> boolToInt(leftValue != rightValue)
                Operator.LOR -> boolToInt(intToBool(leftValue) || intToBool(rightValue))
                Operator.LAND -> boolToInt(intToBool(leftValue) && intToBool(rightValue))
            }
            return InterpretationResult(resultValue, false)
        } catch (e: ArithmeticException) {
            throw FunInterpretationException("Arithmetic exception occurred when computing: " +
                leftValue + " " + op.symbol + " " + rightValue + ".")
        }
    }

    override suspend fun visitIdentifier(identifier: FunAst.Identifier): InterpretationResult =
        InterpretationResult(context.getVariable(identifier).first, false)

    override suspend fun visitNumber(number: FunAst.Number): InterpretationResult {
        try {
            val intValue = number.literal.toInt()
            return InterpretationResult(intValue, false)
        } catch (e: NumberFormatException) {
            throw FunInterpretationException("Number " + number.literal + " is too large.")
        }
    }

    data class InterpretationResult(val value: Int?, val shouldReturn: Boolean)

    companion object {
        val UNIT_INTERPRETATION = InterpretationResult(null, false)

        val DEFAULT_FUNCTION_RESULT = 0
        val PRINTLN_FUN_NAME = "println"

        fun boolToInt(bool: Boolean): Int = if (bool) 1 else 0

        fun intToBool(int: Int): Boolean = int != 0
    }
}