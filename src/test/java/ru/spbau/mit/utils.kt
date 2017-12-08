package ru.spbau.mit

import java.io.ByteArrayOutputStream
import java.io.PrintStream

fun getBytesIfPrinted(vararg lines: String): ByteArray {
    val byteOutputStream = ByteArrayOutputStream()
    val printStream = PrintStream(byteOutputStream)
    for (arg in lines) {
        printStream.println(arg)
    }
    printStream.flush()
    return byteOutputStream.toByteArray()
}