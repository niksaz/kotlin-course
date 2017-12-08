package ru.spbau.mit.debugger

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import ru.spbau.mit.getBytesIfPrinted
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream

class DebuggerTest {
    @Test
    fun debugExample2() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream, true)
        val debugger = FunDebugger(NULL_PRINT_STREAM, printStream)
        debugger.load("src/test/resources/example2.fun")
        debugger.setBreakpointAt(2, null)

        debugger.run()
        assertThat(byteOutputStream.toByteArray()).isEqualTo(getBytesIfPrinted())
        repeat(3) {
            debugger.continueExecution()
            assertThat(byteOutputStream.toByteArray())
                .isEqualTo(getBytesIfPrinted("1 1"))
        }
        repeat(5) {
            debugger.continueExecution()
            assertThat(byteOutputStream.toByteArray())
                .isEqualTo(getBytesIfPrinted("1 1", "2 2"))
        }
        repeat(9) {
            debugger.continueExecution()
            assertThat(byteOutputStream.toByteArray())
                .isEqualTo(getBytesIfPrinted("1 1", "2 2", "3 3"))
        }
        repeat(15) {
            debugger.continueExecution()
            assertThat(byteOutputStream.toByteArray())
                .isEqualTo(getBytesIfPrinted("1 1", "2 2", "3 3", "4 5"))
        }
        debugger.continueExecution()
        assertThat(byteOutputStream.toByteArray())
            .isEqualTo(getBytesIfPrinted("1 1", "2 2", "3 3", "4 5", "5 8"))
    }

    @Test
    fun debugWithConditionsExample2() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream, true)
        val debugger = FunDebugger(NULL_PRINT_STREAM, printStream)
        debugger.load("src/test/resources/example2.fun")
        debugger.setBreakpointAt(2, "n==5")

        debugger.run()
        assertThat(byteOutputStream.toByteArray())
            .isEqualTo(getBytesIfPrinted("1 1", "2 2", "3 3", "4 5"))
        debugger.continueExecution()
        assertThat(byteOutputStream.toByteArray())
            .isEqualTo(getBytesIfPrinted("1 1", "2 2", "3 3", "4 5", "5 8"))
    }

    @Test(expected = FunDebugException::class)
    fun continueWhenFinished() {
        val debugger = FunDebugger(NULL_PRINT_STREAM, NULL_PRINT_STREAM)
        debugger.load("src/test/resources/example1.fun")
        debugger.run()
        debugger.continueExecution()
    }

    @Test(expected = FunDebugException::class)
    fun breakpointOutsideOfControlFlow() {
        val debugger = FunDebugger(NULL_PRINT_STREAM, NULL_PRINT_STREAM)
        debugger.load("src/test/resources/example1.fun")
        debugger.setBreakpointAt(4, null)
        debugger.run()
        debugger.continueExecution()
    }

    companion object {
        private val NULL_OUTPUT_STREAM = object : OutputStream() {
            override fun write(b: Int) {
            }
        }

        private val NULL_PRINT_STREAM = PrintStream(NULL_OUTPUT_STREAM)
    }
}
