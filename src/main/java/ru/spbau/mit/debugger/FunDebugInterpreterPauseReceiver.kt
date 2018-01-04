package ru.spbau.mit.debugger

import ru.spbau.mit.interpreter.FunContext
import kotlin.coroutines.experimental.Continuation

/** Able to receive pauses from [FunDebugInterpreter]. */
interface FunDebugInterpreterPauseReceiver {
    fun interpretationPausedWith(pauseSnapshot: ExecutionPauseSnapshot)

    data class ExecutionPauseSnapshot(
        val lineNumber: Int,
        val executionContext: FunContext,
        val executionContinuation: Continuation<Unit>
    )
}