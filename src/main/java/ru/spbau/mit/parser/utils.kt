package ru.spbau.mit.parser

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import ru.spbau.mit.ast.AntlrFunAstBuilder
import ru.spbau.mit.ast.FunAst

typealias ContextExtractor = (FunParser) -> ParserRuleContext

fun buildAstFrom(sourceCodePath: String): FunAst {
    val fileCharStream = CharStreams.fromFileName(sourceCodePath)
    return FunAst(buildNodeFrom(fileCharStream, FunParser::file))
}

fun buildExprFrom(exprText: String): FunAst.Expression {
    val textCharStream = CharStreams.fromString(exprText)
    return buildNodeFrom(textCharStream, FunParser::expression) as FunAst.Expression
}

private fun buildNodeFrom(charStream: CharStream, extractor: ContextExtractor): FunAst.Node {
    val funLexer = FunLexer(charStream)
    val tokens = CommonTokenStream(funLexer)
    val funParser = FunParser(tokens)
    val context = extractor(funParser)
    if (funParser.numberOfSyntaxErrors > 0) {
        throw FunParsingException()
    }
    return AntlrFunAstBuilder().visit(context)
}