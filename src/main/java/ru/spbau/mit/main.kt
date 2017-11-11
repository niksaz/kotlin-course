package ru.spbau.mit

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.ast.FunAstBuilder
import ru.spbau.mit.interpreter.FunInterpretationException
import ru.spbau.mit.interpreter.FunInterpreter
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import ru.spbau.mit.parser.FunParsingException

fun buildAstFor(sourceCodePath: String): FunAst {
    val funLexer = FunLexer(CharStreams.fromFileName(sourceCodePath))
    val tokens = CommonTokenStream(funLexer)
    val funParser = FunParser(tokens)
    val fileContext = funParser.file()
    if (funParser.numberOfSyntaxErrors > 0) {
        throw FunParsingException()
    }
    return FunAstBuilder().buildAstFromContext(fileContext)
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Pass the path to a Fun source file.")
        return
    }
    try {
        val funAst = buildAstFor(args[0])
        val funInterpreter = FunInterpreter()
        funInterpreter.interpretAst(funAst)
    } catch (e: FunParsingException) {
        System.err.println("The code will not be interpreted since parsing errors were met.")
    } catch (e: FunInterpretationException) {
        System.err.println("Exception during the interpretation:")
        System.err.println(e.message)
    }
}