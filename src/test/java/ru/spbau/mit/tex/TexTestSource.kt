package ru.spbau.mit.tex

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TexTestSource {
    @Test
    fun testCustomOutputStream() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream, true)
        document {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            usePackage("hyperref", "hidelinks") {
                +("colorlinks" to "true")
                +("linkcolor" to "blue")
                +("urlcolor" to "blue")
                +("citecolor" to "blue")
                +("anchorcolor" to "blue")
            }
        }.render(printStream)
        val correctBytes = """
            |\documentclass{beamer}
            |\usepackage[russian,english]{babel}
            |\usepackage[hidelinks,anchorcolor=blue,urlcolor=blue,colorlinks=true,citecolor=blue,linkcolor=blue]{hyperref}
            |\begin{document}
            |\end{document}
            |""".trimMargin().toByteArray()
        assertThat(byteOutputStream.toByteArray()).isEqualTo(correctBytes)
    }

    @Test
    fun testDocumentInit() {
        val doc =
            document {
                documentClass("beamer")
                usePackage("babel", "russian", "english")
                usePackage("hyperref", "hidelinks") {
                    +("colorlinks" to "true")
                    +("linkcolor" to "blue")
                    +("urlcolor" to "blue")
                    +("citecolor" to "blue")
                    +("anchorcolor" to "blue")
                }
            }
        assertThat(doc.toString()).isEqualTo("""
            |\documentclass{beamer}
            |\usepackage[russian,english]{babel}
            |\usepackage[hidelinks,anchorcolor=blue,urlcolor=blue,colorlinks=true,citecolor=blue,linkcolor=blue]{hyperref}
            |\begin{document}
            |\end{document}
            |""".trimMargin())
    }

    @Test(expected = DocumentClassException::class)
    fun testNoDocumentClass() {
        document {
            usePackage("babel", "russian", "english")
        }
    }

    @Test(expected = DocumentClassException::class)
    fun testTwoDocumentClasses() {
        document {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            documentClass("amsart")
        }
    }

    @Test
    fun testItemize() {
        val doc =
            document {
                documentClass("beamer")
                    itemize {
                        item {
                            +"Not to be driven this way and that,"
                            +"but always to behave with justice and see things as they are."
                        }
                        item {
                            +"To the world: Your harmony is mine."
                            +"Whatever time you choose is the right time. Not late, not early."
                        }
                    }
            }
        assertThat(doc.toString()).isEqualTo("""
            |\documentclass{beamer}
            |\begin{document}
            |\begin{itemize}
            |\item
            |Not to be driven this way and that,
            |but always to behave with justice and see things as they are.
            |\item
            |To the world: Your harmony is mine.
            |Whatever time you choose is the right time. Not late, not early.
            |\end{itemize}
            |\end{document}
            |""".trimMargin())
    }

    @Test
    fun testEnumerate() {
        val doc =
            document {
                documentClass("beamer")
                enumerate {
                    item {
                        +"You’ve seen that. Now look at this."
                        +"Don’t be disturbed. Uncomplicate yourself."
                    }
                    item {
                        +"Character: dark, womanish, obstinate."
                        +"Wolf, sheep, child, fool, cheat, buffoon, salesman, tyrant."
                    }
                }
            }
        assertThat(doc.toString()).isEqualTo("""
            |\documentclass{beamer}
            |\begin{document}
            |\begin{enumerate}
            |\item
            |You’ve seen that. Now look at this.
            |Don’t be disturbed. Uncomplicate yourself.
            |\item
            |Character: dark, womanish, obstinate.
            |Wolf, sheep, child, fool, cheat, buffoon, salesman, tyrant.
            |\end{enumerate}
            |\end{document}
            |""".trimMargin())
    }

    @Test
    fun testFrame() {
        val doc =
            document {
                documentClass("beamer")
                frame(frameTitle = "Hello world with C++") {
                    +"""
                     |int main()"
                     |{
                     |   printf("Hello World!");
                     |   return 0;
                     |}
                     """.trimMargin()
                }
            }
        assertThat(doc.toString()).isEqualTo("""
            |\documentclass{beamer}
            |\begin{document}
            |\begin{frame}
            |\frametitle{Hello world with C++}
            |int main()"
            |{
            |   printf("Hello World!");
            |   return 0;
            |}
            |\end{frame}
            |\end{document}
            |""".trimMargin())
    }

    @Test
    fun testMath() {
        val doc =
            document {
                documentClass("beamer")
                math("x+10")
                math("\\sum_{i=1}^{10}i^3?")
            }
        assertThat(doc.toString()).isEqualTo("""
            |\documentclass{beamer}
            |\begin{document}
            |\math{x+10}
            |\math{\sum_{i=1}^{10}i^3?}
            |\end{document}
            |""".trimMargin())
    }

    @Test
    fun testAlignment() {
        val doc =
            document {
                documentClass("amsart")
                alignment {
                    center {
                        +"What is the right answer?"
                    }
                    left {
                        math("x=10")
                    }
                    right {
                        math("x=17")
                    }
                }
            }
        assertThat(doc.toString()).isEqualTo("""
            |\documentclass{amsart}
            |\begin{document}
            |\begin{alignment}
            |\center
            |What is the right answer?
            |\left
            |\math{x=10}
            |\right
            |\math{x=17}
            |\end{alignment}
            |\end{document}
            |""".trimMargin())
    }

    @Test
    fun testComplexDocument() {
        val rows = listOf(
            "Love the discipline you know, and let it support you.",
            "Entrust everything willingly to the gods, and then make your way through life-",
            "no one’s master and no one’s slave.")
        val doc =
            document {
                documentClass("beamer")
                usePackage("babel", "russian")
                frame(frameTitle = "frametitle") {
                    +("arg1" to "arg2")
                    itemize {
                        for (row in rows) {
                            item { +row }
                        }
                    }
                }
                customTag(name = "pyglist") {
                    +("language" to "kotlin")
                    +"""
                     |val a = 1
                     |println(a)
                     """.trimMargin()
                }
            }
        assertThat(doc.toString()).isEqualTo("""
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |\begin{frame}[arg1=arg2]
            |\frametitle{frametitle}
            |\begin{itemize}
            |\item
            |Love the discipline you know, and let it support you.
            |\item
            |Entrust everything willingly to the gods, and then make your way through life-
            |\item
            |no one’s master and no one’s slave.
            |\end{itemize}
            |\end{frame}
            |\begin{pyglist}[language=kotlin]
            |val a = 1
            |println(a)
            |\end{pyglist}
            |\end{document}
            |""".trimMargin())
    }
}