package ru.spbau.mit

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.spbau.mit.ast.FunAst
import ru.spbau.mit.ast.FunAstBuilder
import ru.spbau.mit.interpreter.FunInterpreter
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser

fun buildAstFor(sourceCodePath: String): FunAst {
    val funLexer = FunLexer(CharStreams.fromFileName(sourceCodePath))
    val tokens = CommonTokenStream(funLexer)
    val funParser = FunParser(tokens)
    return FunAstBuilder().buildAstFromContext(funParser.file())
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Pass the path to a Fun source file.")
        return
    }
    val funAst = buildAstFor(args[0])
    val funInterpreter = FunInterpreter()
    funInterpreter.interpretAst(funAst)
}