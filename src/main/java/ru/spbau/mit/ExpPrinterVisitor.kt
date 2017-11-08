package ru.spbau.mit

import ru.spbau.mit.parser.ExpBaseVisitor
import ru.spbau.mit.parser.ExpParser

class ExpPrinterVisitor : ExpBaseVisitor<Unit>() {
    override fun visitEval(ctx: ExpParser.EvalContext) {
        println("EvalContext")
        visitChildren(ctx)
    }

    override fun visitAdditionExp(ctx: ExpParser.AdditionExpContext) {
        println("AdditionExpContext")
        visitChildren(ctx)
    }

    override fun visitMultiplyExp(ctx: ExpParser.MultiplyExpContext) {
        println("MultiplyExpContext")
        visitChildren(ctx)
    }

    override fun visitAtomExp(ctx: ExpParser.AtomExpContext) {
        if (ctx.Number() != null) {
            println("AtomExpContext: " + ctx.text)
        }
        visitChildren(ctx)
    }
}