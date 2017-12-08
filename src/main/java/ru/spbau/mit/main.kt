package ru.spbau.mit

import kotlinx.coroutines.experimental.runBlocking
import ru.spbau.mit.interpreter.FunInterpretationException
import ru.spbau.mit.interpreter.FunInterpreter
import ru.spbau.mit.parser.FunParsingException
import ru.spbau.mit.parser.buildAstFrom
import java.io.PrintStream

fun interpretSourceFile(
    sourceCodePath: String, printStream: PrintStream = System.out
): FunInterpreter.InterpretationResult {
    val funAst = buildAstFrom(sourceCodePath)
    val funInterpreter = FunInterpreter(printStream)
    return runBlocking { funInterpreter.interpretAst(funAst) }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Pass the path to a Fun source file.")
        return
    }
    try {
        interpretSourceFile(args[0])
    } catch (e: FunParsingException) {
        System.err.println("The code will not be interpreted since parsing errors were met.")
    } catch (e: FunInterpretationException) {
        System.err.println("Exception during the interpretation: ${e.message}.")
    }
}