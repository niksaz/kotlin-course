package ru.spbau.mit.debugger

import ru.spbau.mit.interpreter.FunContext
import ru.spbau.mit.interpreter.FunInterpreter
import kotlin.coroutines.experimental.Continuation

/** Able to receive events from [FunDebugInterpreter]. */
interface FunDebugInterpreterReceiver {
    fun interpretationFinishedWith(result: FunInterpreter.InterpretationResult)

    fun interpretationPausedWith(pauseSnapshot: ExecutionPauseSnapshot)

    data class ExecutionPauseSnapshot(
        val lineNumber: Int,
        val executionContext: FunContext,
        val executionContinuation: Continuation<Unit>
    )
}