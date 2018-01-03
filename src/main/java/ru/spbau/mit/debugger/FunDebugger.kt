package ru.spbau.mit.debugger

import kotlinx.coroutines.experimental.runBlocking
import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.debugger.FunDebugInterpreterReceiver.ExecutionPauseSnapshot
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
) : FunDebugInterpreterReceiver {
    private var loadedAst: FunAst? = null

    private val breakpointMap: MutableMap<Int, FunAst.Expression?> = hashMapOf()

    private var pauseSnapshot: ExecutionPauseSnapshot? = null
    private var result: InterpretationResult? = null

    override fun interpretationFinishedWith(result: InterpretationResult) {
        this.result = result
        pauseSnapshot = null
    }

    override fun interpretationPausedWith(pauseSnapshot: ExecutionPauseSnapshot) {
        this.pauseSnapshot = pauseSnapshot
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
        requireProgramNonRunning()
        val debugInterpreter = FunDebugInterpreter(this, breakpointMap, programOut)
        pauseSnapshot = ExecutionPauseSnapshot(
            0, FunContext(), createInitialContinuation {
                debugInterpreter.interpretAstDebugMode(loadedAst!!)
            })
        continueExecution()
    }

    private fun createInitialContinuation(startLambda: suspend () -> Unit): Continuation<Unit> =
        startLambda.createCoroutine(object : Continuation<Unit> {
            override val context: CoroutineContext
                get() = EmptyCoroutineContext

            override fun resume(value: Unit) {
            }

            override fun resumeWithException(exception: Throwable) {
            }
        })

    fun stop() {
        requireAstLoaded()
        requireProgramRunning()
        pauseSnapshot = null
        debugOut.println("Stopped execution.")
    }

    fun evaluate(exprText: String) {
        requireAstLoaded()
        requireProgramRunning()
        val expr = buildExprFrom(exprText)
        val exprResult = runBlocking {
            FunInterpreter(debugOut, pauseSnapshot!!.executionContext.copy()).visit(expr)
        }
        debugOut.println("Result: ${exprResult.value!!}")
    }

    fun continueExecution() {
        requireAstLoaded()
        requireProgramRunning()
        pauseSnapshot!!.executionContinuation.resume(Unit)
        if (pauseSnapshot != null) {
            debugOut.println("Program was paused on line ${pauseSnapshot!!.lineNumber}.")
        } else {
            val returnCode = result!!.value
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

    private fun requireAstLoaded() {
        if (loadedAst == null) {
            throw FunDebugException("No file is loaded right now: use \"load <filename>\"!")
        }
    }

    private fun requireProgramRunning() {
        if (pauseSnapshot == null) {
            throw FunDebugException("No program is running.")
        }
    }

    private fun requireProgramNonRunning() {
        if (pauseSnapshot != null) {
            throw FunDebugException("Program is already running!")
        }
    }
}