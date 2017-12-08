package ru.spbau.mit

import ru.spbau.mit.debugger.FunDebugger
import ru.spbau.mit.debugger.FunDebugException
import java.io.BufferedReader
import java.io.PrintStream

typealias CommandHandler = (List<String>) -> Unit

/** Specifies for which string arguments the given [CommandHandler] should be called. */
class CommandDescriptor(
    private val commandName: String,
    private val commandArgsCount: Int,
    val helpHint: String,
    private val handler: CommandHandler
) {
    fun mayConsume(args: List<String>): Boolean =
        args.isNotEmpty() && args[0] == commandName && args.size - 1 >= commandArgsCount

    /** Should be called only if [mayConsume] for [args] is true. */
    fun process(args: List<String>) {
        require(mayConsume(args))
        handler(args)
    }
}

fun buildDescriptors(out: PrintStream): List<CommandDescriptor> {
    val debugger = FunDebugger(out)
    val primeDescriptors = mutableListOf(
        CommandDescriptor("load", 1, "load filename") { args ->
            debugger.load(args[1])
        },
        CommandDescriptor("breakpoint", 1, "breakpoint line-number") { args ->
            debugger.setBreakpointAt(args[1].toInt(), null)
        },
        CommandDescriptor("condition", 2, "condition line-number condition-expression") { args ->
            debugger.setBreakpointAt(args[1].toInt(), args.drop(2).joinToString(" "))
        },
        CommandDescriptor("list", 0, "list") {
            debugger.listBreakpoints()
        },
        CommandDescriptor("remove", 1, "remove line-number") { args ->
            debugger.removeBreakpointAt(args[1].toInt())
        },
        CommandDescriptor("run", 0, "run") {
            debugger.run()
        },
        CommandDescriptor("stop", 0, "stop") {
            debugger.stop()
        },
        CommandDescriptor("evaluate", 1, "evaluate expression") { args ->
            debugger.evaluate(args.drop(1).joinToString(" "))
        },
        CommandDescriptor("continue", 0, "continue") {
            debugger.continueExecution()
        },
        CommandDescriptor("pwd", 0, "pwd") {
            out.println(System.getProperty("user.dir"))
        }
    )
    primeDescriptors.add(CommandDescriptor("help", 0, "help") {
        out.println("Commands:")
        primeDescriptors.forEach { out.println(it.helpHint) }
    })
    return primeDescriptors
}

private const val PROMPT = "> "

fun enterRepl(descriptors: List<CommandDescriptor>, input: BufferedReader, out: PrintStream) {
    while (true) {
        out.print(PROMPT)
        val line = input.readLine()?.trim() ?: break
        val lineArgs = line.split(Regex("\\s+"))
        if (lineArgs.size == 1 && lineArgs[0] == "quit") {
            break
        }
        val descriptor = descriptors.find { it.mayConsume(lineArgs) }
        if (descriptor == null) {
            out.println("Unknown command: ${lineArgs.joinToString(" ")}")
        } else {
            try {
                descriptor.process(lineArgs)
            } catch(e: FunDebugException) {
                out.println(e.message)
            } catch (e: Exception) {
                out.println("Exception during command execution:")
                e.printStackTrace(out)
            }
        }
    }
}

fun main(args: Array<String>) {
    val out = System.out
    val descriptors = buildDescriptors(out)
    enterRepl(descriptors, System.`in`.bufferedReader(), out)
}