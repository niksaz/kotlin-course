package ru.spbau.mit

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.ast.AntlrFunAstBuilder
import ru.spbau.mit.interpreter.FunInterpretationException
import ru.spbau.mit.interpreter.FunInterpreter
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import ru.spbau.mit.parser.FunParsingException
import java.io.PrintStream

fun buildAstFor(sourceCodePath: String): FunAst {
    val funLexer = FunLexer(CharStreams.fromFileName(sourceCodePath))
    val tokens = CommonTokenStream(funLexer)
    val funParser = FunParser(tokens)
    val fileContext = funParser.file()
    if (funParser.numberOfSyntaxErrors > 0) {
        throw FunParsingException()
    }
    return AntlrFunAstBuilder().buildAstFromContext(fileContext)
}

fun interpretSourceFile(
    sourceCodePath: String, printStream: PrintStream = System.out
): FunInterpreter.InterpretationResult {
    val funAst = buildAstFor(sourceCodePath)
    val funInterpreter = FunInterpreter(printStream)
    return funInterpreter.interpretAst(funAst)
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
        System.err.println("Exception during the interpretation:")
        System.err.println(e.message)
    }
}