package ru.spbau.mit.interpreter

import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.ast.FunAst.Operator
import ru.spbau.mit.ast.FunAstBaseVisitor
import ru.spbau.mit.interpreter.FunInterpreter.InterpretationResult
import java.io.PrintStream

/** A visitor for interpreting the [FunAst] nodes. */
class FunInterpreter(
    private val outputStream: PrintStream = System.out,
    private val context: FunContext = FunContext(setOf(PRINTLN_IDENTIFIER))
) : FunAstBaseVisitor<InterpretationResult> {
    fun interpretAst(ast: FunAst): InterpretationResult {
        return visit(ast.rootNode)
    }

    override fun visitFile(file: FunAst.File): InterpretationResult {
        return visitBlock(file.block)
    }

    override fun visitBlock(block: FunAst.Block): InterpretationResult {
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

    override fun visitFunction(function: FunAst.Function): InterpretationResult {
        context.declareFunction(function)
        return UNIT_INTERPRETATION
    }

    override fun visitVariable(variable: FunAst.Variable): InterpretationResult {
        val expression = variable.expression
        if (expression != null) {
            val interpretedExpression = visit(expression)
            context.declareVariable(variable.identifier, interpretedExpression.value!!)
        } else {
            context.declareVariable(variable.identifier, null)
        }
        return UNIT_INTERPRETATION
    }

    override fun visitParameterNames(parameterNames: FunAst.ParameterNames): InterpretationResult {
        return UNIT_INTERPRETATION
    }

    override fun visitWhileBlock(whileBlock: FunAst.WhileBlock): InterpretationResult {
        while (visit(whileBlock.condition).value!! != 0) {
            val interpretedBody = visit(whileBlock.body)
            if (interpretedBody.shouldReturn) {
                return interpretedBody
            }
        }
        return UNIT_INTERPRETATION
    }

    override fun visitIfStatement(ifStatement: FunAst.IfStatement): InterpretationResult {
        return when {
            visit(ifStatement.condition).value!! != 0 -> visit(ifStatement.body)
            ifStatement.elseBody != null -> visit(ifStatement.elseBody)
            else -> UNIT_INTERPRETATION
        }
    }

    override fun visitAssignment(assignment: FunAst.Assignment): InterpretationResult {
        val scope = context.getVariable(assignment.identifier).second
        context.setVariable(scope, assignment.identifier, visit(assignment.expression).value!!)
        return UNIT_INTERPRETATION
    }

    override fun visitReturnStatement(
        returnStatement: FunAst.ReturnStatement
    ): InterpretationResult {
        return InterpretationResult(visit(returnStatement.expression).value, true)
    }

    override fun visitFunctionCall(functionCall: FunAst.FunctionCall): InterpretationResult {
        val function = context.getFunction(
            functionCall.identifier, functionCall.arguments.expressions.size)
        val args = functionCall.arguments.expressions.map { visit(it).value!! }
        return if (function != null) {
            context.enterScope()
            val paramIterator = function.paramNames.params.listIterator()
            for (arg in args) {
                val param = paramIterator.next()
                context.declareVariable(param, arg)
            }
            val interpretedBody = visit(function.body)
            context.leaveScope()
            InterpretationResult(
                if (interpretedBody.shouldReturn) interpretedBody.value!! else DEFAULT_RESULT,
                false
            )
        } else {
            runBuiltItFunction(functionCall.identifier, args)
        }
    }

    private fun runBuiltItFunction(
        identifier: FunAst.Identifier, args: List<Int>
    ): InterpretationResult {
        if (identifier == PRINTLN_IDENTIFIER) {
            val argsString = args.joinToString(" ") { it.toString() }
            outputStream.println(argsString)
            return UNIT_INTERPRETATION
        }
        throw FunInterpretationException("Unknown built-in function " + identifier.name + ".")
    }

    override fun visitArguments(arguments: FunAst.Arguments): InterpretationResult {
        return UNIT_INTERPRETATION
    }

    override fun visitBinaryExpression(
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

    override fun visitIdentifier(identifier: FunAst.Identifier): InterpretationResult {
        return InterpretationResult(context.getVariable(identifier).first, false)
    }

    override fun visitNumber(number: FunAst.Number): InterpretationResult {
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

        private val DEFAULT_RESULT = 0
        private val PRINTLN_IDENTIFIER = FunAst.Identifier("println")

        private fun boolToInt(bool: Boolean): Int {
            return if (bool) 1 else 0
        }

        private fun intToBool(int: Int): Boolean {
            return int != 0
        }
    }
}