package ru.spbau.mit.tex

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TexTestSource {
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
}