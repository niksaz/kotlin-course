package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.interpreter.FunInterpreter.Companion.UNIT_INTERPRETATION

class IntegrationTest {
    @Test
    fun testExample1() {
        InterpretationVerifier(UNIT_INTERPRETATION, "0")
            .verifySourceFileInterpretation("src/test/resources/example1.fun")
    }

    @Test
    fun testExample2() {
        InterpretationVerifier(UNIT_INTERPRETATION, "1 1", "2 2", "3 3", "4 5", "5 8")
            .verifySourceFileInterpretation("src/test/resources/example2.fun")
    }

    @Test
    fun testExample3() {
        InterpretationVerifier(UNIT_INTERPRETATION, "42")
            .verifySourceFileInterpretation("src/test/resources/example3.fun")
    }

    @Test
    fun testArithmetic() {
        InterpretationVerifier(UNIT_INTERPRETATION, "5")
            .verifySourceFileInterpretation("src/test/resources/arithmetic.fun")
    }
}