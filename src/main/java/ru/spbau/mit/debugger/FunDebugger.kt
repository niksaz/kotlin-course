package ru.spbau.mit.debugger

import kotlinx.coroutines.experimental.runBlocking
import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.debugger.FunDebugInterpreterPauseReceiver.ExecutionPauseSnapshot
import ru.spbau.mit.interpreter.FunContext
import ru.spbau.mit.interpreter.FunInterpreter
import ru.spbau.mit.interpreter.FunInterpreter.InterpretationResult
import ru.spbau.mit.parser.buildAstFrom
import ru.spbau.mit.parser.buildExprFrom
import java.io.PrintStream
import kotlin.coroutines.experimental.*

class FunDebugger(
    private val debugOut: PrintStream,
    private val programOut: PrintStream = debugOut
) : FunDebugInterpreterPauseReceiver {
    private val breakpointMap: MutableMap<Int, FunAst.Expression?> = hashMapOf()

    private var loadedAst: FunAst? = null
    private var interpreterState: InterpreterState? = null

    override fun interpretationPausedWith(pauseSnapshot: ExecutionPauseSnapshot) {
        interpreterState = PausedState(pauseSnapshot)
    }

    fun load(filename: String) {
        loadedAst = buildAstFrom(filename)
        breakpointMap.clear()
        debugOut.println("Successfully built AST for $filename")
    }

    fun setBreakpointAt(lineNumber: Int, conditionExpr: String?) {
        requireAstLoaded()
        breakpointMap.put(lineNumber, conditionExpr?.let { buildExprFrom(it) })
        debugOut.println("Breakpoint is set on $lineNumber")
    }

    fun listBreakpoints() {
        requireAstLoaded()
        debugOut.println("Breakpoints:")
        breakpointMap.forEach { lineNumber, expr ->
            debugOut.println("@$lineNumber $expr")
        }
    }

    fun removeBreakpointAt(lineNumber: Int) {
        requireAstLoaded()
        breakpointMap.remove(lineNumber)
        debugOut.println("Removed all breakpoints from $lineNumber")
    }

    fun run() {
        requireAstLoaded()
        requireProgramRunning(false)
        val debugInterpreter = FunDebugInterpreter(this, breakpointMap, programOut)
        interpreterState = PausedState(ExecutionPauseSnapshot(
            0, FunContext(), createInitialContinuation {
                debugInterpreter.interpretAstDebugMode(loadedAst!!)
            }))
        continueExecution()
    }

    private fun createInitialContinuation(
        startLambda: suspend () -> InterpretationResult
    ): Continuation<Unit> =
        startLambda.createCoroutine(object : Continuation<InterpretationResult> {
            override val context: CoroutineContext
                get() = EmptyCoroutineContext

            override fun resume(value: InterpretationResult) {
                interpreterState = FinishedState(value)
            }

            override fun resumeWithException(exception: Throwable) {
            }
        })

    fun stop() {
        requireAstLoaded()
        requireProgramRunning(true)
        interpreterState = null
        debugOut.println("Stopped execution.")
    }

    fun evaluate(exprText: String) {
        requireAstLoaded()
        requireProgramRunning(true)
        val expr = buildExprFrom(exprText)
        val exprResult = runBlocking {
            FunInterpreter(
                debugOut,
                (interpreterState!! as PausedState).pauseSnapshot.executionContext.clone())
            .visit(expr)
        }
        debugOut.println("Result: ${exprResult.value!!}")
    }

    fun continueExecution() {
        requireAstLoaded()
        requireProgramRunning(true)
        (interpreterState!! as PausedState).pauseSnapshot.executionContinuation.resume(Unit)
        val resumedInterpreterState = interpreterState!!
        when (resumedInterpreterState) {
            is PausedState -> {
                debugOut.println("Program was paused on line " +
                    "${resumedInterpreterState.pauseSnapshot.lineNumber}.")
            }
            is FinishedState -> {
                val returnCode = resumedInterpreterState.result.value
                debugOut.println(
                    "Program finished with " +
                        if (returnCode == null) {
                            "no return value."
                        }
                        else {
                            "return value $returnCode."
                        })
            }
        }
    }

    private fun requireAstLoaded() {
        if (loadedAst == null) {
            throw FunDebugException("No file is loaded right now: use \"load <filename>\"!")
        }
    }

    private fun requireProgramRunning(requiredRunning: Boolean) {
        val isRunning = interpreterState != null && interpreterState is PausedState
        if (isRunning != requiredRunning) {
            throw FunDebugException(
                if (isRunning) "Program is already running!" else "No program is running.")
        }
    }
}

private sealed class InterpreterState

private data class PausedState(val pauseSnapshot: ExecutionPauseSnapshot) : InterpreterState()

private data class FinishedState(val result: InterpretationResult) : InterpreterState()