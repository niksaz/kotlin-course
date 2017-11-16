package ru.spbau.mit.tex

@DslMarker
annotation class TexElementMarker

@TexElementMarker
abstract class Element(val name: String) {
    private val options = hashMapOf<String, String>()

    abstract fun render(builder: StringBuilder, indent: String = "")

    protected fun buildOptions(): List<String> = options.map { it.key + "=" + it.value }

    operator fun Pair<String, String>.unaryPlus() {
        options.put(first, second)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder)
        return builder.toString()
    }
}

abstract class Tag(name: String) : Element(name) {
    private val children = arrayListOf<Element>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit = {}): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}\n")
        for (child in children) {
            child.render(builder, indent + "  ")
        }
        builder.append("$indent\\end{$name}\n")
    }
}

abstract class Command(
    name: String,
    private val mainArg: String,
    private val additionalArgs: Array<out String>
) : Element(name) {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\$name")
        val parts = mutableListOf<String>()
        parts.addAll(additionalArgs)
        parts.addAll(buildOptions())
        if (parts.isNotEmpty()) {
            builder.append(parts.joinToString(",", "[", "]"))
        }
        builder.append("{$mainArg}\n")
    }
}

class UsePackage(
    mainArg: String,
    additionalArgs: Array<out String>
) : Command("usepackage", mainArg, additionalArgs)

class DocumentClass(
    mainArg: String,
    additionalArgs: Array<out String>
) : Command("documentclass", mainArg, additionalArgs)

class Document : Tag("document") {
    private val usePackages = mutableListOf<UsePackage>()

    var documentClass: DocumentClass? = null
        private set(value) {
            field = value
        }

    override fun render(builder: StringBuilder, indent: String) {
        documentClass!!.render(builder, indent)
        usePackages.forEach {
            it.render(builder, indent)
        }
        super.render(builder, indent)
    }

    fun usePackage(
        mainArg: String,
        vararg additionalArgs: String,
        init: UsePackage.() -> Unit = {}
    ) {
        val usePackage = UsePackage(mainArg, additionalArgs)
        usePackage.init()
        usePackages.add(usePackage)
    }

    fun documentClass(
        mainArg: String,
        vararg additionalArgs: String,
        init: DocumentClass.() -> Unit = {}) {
        if (documentClass != null) {
            throw DocumentClassException("Another documentclass was met inside document!")
        }
        val documentClass = DocumentClass(mainArg, additionalArgs)
        documentClass.init()
        this.documentClass = documentClass
    }
}

fun document(init: Document.() -> Unit = {}): Document {
    val document = Document()
    document.init()
    if (document.documentClass == null) {
        throw DocumentClassException("No documentclass command for document!")
    }
    return document
}