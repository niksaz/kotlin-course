package ru.spbau.mit.tex

@DslMarker
annotation class TexElementMarker

@TexElementMarker
abstract class Element {
    private val options = hashMapOf<String, String>()

    abstract fun render(appendable: Appendable)

    protected fun buildOptions(): List<String> = options.map { it.key + "=" + it.value }

    protected fun Appendable.appendOptions() {
        val parts = buildOptions()
        if (parts.isNotEmpty()) {
            append(parts.joinToString(",", "[", "]"))
        }
        append('\n')
    }

    operator fun Pair<String, String>.unaryPlus() {
        options.put(first, second)
    }

    override fun toString(): String = buildString(this::render)
}

class TextElement(private val text: String) : Element() {
    override fun render(appendable: Appendable) {
        appendable.append("$text\n")
    }
}

abstract class Tag(private val name: String) : Element() {
    val children = arrayListOf<Element>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(appendable: Appendable) {
        appendable.append("\\begin{$name}")
        appendable.appendOptions()
        for (child in children) {
            child.render(appendable)
        }
        appendable.append("\\end{$name}\n")
    }
}

abstract class ListingTag(name: String) : Tag(name) {
    fun item(init: ListingItem.() -> Unit) = initTag(ListingItem("item"), init)
}

abstract class Command(
    private val name: String,
    private val mainArg: String,
    private val additionalArgs: Array<out String>
) : Element() {
    override fun render(appendable: Appendable) {
        appendable.append("\\$name")
        val parts = mutableListOf<String>()
        parts.addAll(additionalArgs)
        parts.addAll(buildOptions())
        if (parts.isNotEmpty()) {
            appendable.append(parts.joinToString(",", "[", "]"))
        }
        appendable.append("{$mainArg}\n")
    }
}

class Math(
    formula: String,
    additionalArgs: Array<out String>
) : Command("math", formula, additionalArgs)

class FrameTitle(title: String) : Command("frametitle", title, emptyArray())

class UsePackage(
    mainArg: String,
    additionalArgs: Array<out String>
) : Command("usepackage", mainArg, additionalArgs)

class DocumentClass(
    mainArg: String,
    additionalArgs: Array<out String>
) : Command("documentclass", mainArg, additionalArgs)

class Itemize : ListingTag("itemize")

class Enumerate : ListingTag("enumerate")

abstract class TagWithContent(name: String) : Tag(name) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    fun math(formula: String, vararg additionalArgs: String) {
        children.add(Math(formula, additionalArgs))
    }

    fun alignment(init: Alignment.() -> Unit) = initTag(Alignment(), init)

    fun frame(frameTitle: String, init: Frame.() -> Unit) = initTag(Frame(frameTitle), init)

    fun customTag(name: String, init: CustomTag.() -> Unit) = initTag(CustomTag(name), init)

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)
}

class Alignment : Tag("alignment") {
    fun left(init: ListingItem.() -> Unit) = initTag(ListingItem("left"), init)

    fun right(init: ListingItem.() -> Unit) = initTag(ListingItem("right"), init)

    fun center(init: ListingItem.() -> Unit) = initTag(ListingItem("center"), init)
}

class ListingItem(private val name: String) : TagWithContent(name) {
    override fun render(appendable: Appendable) {
        appendable.append("\\$name")
        appendable.appendOptions()
        for (child in children) {
            child.render(appendable)
        }
    }
}

class Frame(frameTitle: String) : TagWithContent("frame") {
    init {
        children.add(FrameTitle(frameTitle))
    }
}

class CustomTag(name: String) : TagWithContent(name)

class Document : TagWithContent("document") {
    private val usePackages = mutableListOf<UsePackage>()

    var documentClass: DocumentClass? = null
        private set(value) {
            field = value
        }

    override fun render(appendable: Appendable) {
        documentClass!!.render(appendable)
        usePackages.forEach { it.render(appendable) }
        super.render(appendable)
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

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    if (document.documentClass == null) {
        throw DocumentClassException("No documentclass command for document!")
    }
    return document
}