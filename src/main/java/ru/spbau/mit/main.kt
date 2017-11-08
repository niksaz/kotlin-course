package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.spbau.mit.parser.ExpLexer
import ru.spbau.mit.parser.ExpParser

fun getGreeting(): String {
    val words = mutableListOf<String>()
    words.add("Hello,")
    
    words.add("world!")

    return words.joinToString(separator = " ")
}

fun main(args: Array<String>) {
    val expLexer = ExpLexer(CharStreams.fromString("(1 + 2) + 3"))
    val commonTokenStream = CommonTokenStream(expLexer)
    val expParser = ExpParser(commonTokenStream)
    val e = expParser.eval()
    ExpPrinterVisitor().visit(e)

//    for (t in c) {
//        print(t.tokenSource)
//        println(t)
//    }
//    println(ExpParser(BufferedTokenStream(expLexer)).eval().value)
}
