package ru.spbau.mit

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.spbau.mit.ast.FunAstBuilder
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Pass the path to a Fun source file")
        return
    }
    val funLexer = FunLexer(CharStreams.fromFileName(args[0]))
    val tokens = CommonTokenStream(funLexer)
    val parser = FunParser(tokens)
    val funAst = FunAstBuilder().buildAstFromContext(parser.file())
    println(funAst)
}
