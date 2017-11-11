package ru.spbau.mit.interpreter

/** [RuntimeException] for the exceptions during the AST interpretation. */
data class FunInterpretationException(override val message: String) : RuntimeException(message)