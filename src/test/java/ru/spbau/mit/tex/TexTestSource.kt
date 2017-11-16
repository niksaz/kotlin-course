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
}