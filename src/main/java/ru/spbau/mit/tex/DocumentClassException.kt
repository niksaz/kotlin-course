package ru.spbau.mit.tex

/** Thrown then there is other than one [DocumentClass] inside [Document]. */
data class DocumentClassException(override val message: String) : RuntimeException(message)