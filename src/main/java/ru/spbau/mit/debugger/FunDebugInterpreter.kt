package ru.spbau.mit.debugger

import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.debugger.FunDebugInterpreterPauseReceiver.ExecutionPauseSnapshot
import ru.spbau.mit.interpreter.FunInterpreter
import java.io.PrintStream
import kotlin.coroutines.experimental.suspendCoroutine

/** A visitor for interpreting the [FunAst] nodes in DEBUG mode. */
class FunDebugInterpreter(
    private val pauseReceiver: FunDebugInterpreterPauseReceiver,
    private val breakpointMap: Map<Int, FunAst.Expression?>,
    printStream: PrintStream
) : FunInterpreter(printStream) {
    suspend fun interpretAstDebugMode(ast: FunAst): InterpretationResult = visit(ast.rootNode)

    suspend private fun processBreakpointsAt(lineNumber: Int) {
        if (breakpointMap.contains(lineNumber)) {
            val condition = breakpointMap[lineNumber]
            if (condition != null) {
                val interpreter = FunInterpreter(printStream, context.clone())
                try {
                    val interpretationResult = interpreter.visit(condition)
                    val conditionHolds = intToBool(interpretationResult.value!!)
                    if (!conditionHolds) {
                        return
                    }
                } catch (e: Exception) {
                    printStream.println(
                        "Could not interpret specified condition ${e.message}:.")
                }
            }
            suspendCoroutine<Unit> { continuation ->
                pauseReceiver.interpretationPausedWith(
                    ExecutionPauseSnapshot(lineNumber, context.clone(), continuation))
            }
        }
    }

    override suspend fun visitVariable(variable: FunAst.Variable): InterpretationResult {
        processBreakpointsAt(variable.lineNumber)
        return super.visitVariable(variable)
    }

    override suspend fun visitWhileBlock(whileBlock: FunAst.WhileBlock): InterpretationResult {
        processBreakpointsAt(whileBlock.lineNumber)
        return super.visitWhileBlock(whileBlock)
    }

    override suspend fun visitIfStatement(
        ifStatement: FunAst.IfStatement
    ): InterpretationResult {
        processBreakpointsAt(ifStatement.lineNumber)
        return super.visitIfStatement(ifStatement)
    }

    override suspend fun visitAssignment(assignment: FunAst.Assignment): InterpretationResult {
        processBreakpointsAt(assignment.lineNumber)
        return super.visitAssignment(assignment)
    }

    override suspend fun visitReturnStatement(
        returnStatement: FunAst.ReturnStatement
    ): InterpretationResult {
        processBreakpointsAt(returnStatement.lineNumber)
        return super.visitReturnStatement(returnStatement)
    }

    override suspend fun visitFunctionCall(
        functionCall: FunAst.FunctionCall
    ): InterpretationResult {
        processBreakpointsAt(functionCall.lineNumber)
        return super.visitFunctionCall(functionCall)
    }
}