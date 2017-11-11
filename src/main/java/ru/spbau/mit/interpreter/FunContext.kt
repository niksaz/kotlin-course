package ru.spbau.mit.interpreter

import ru.spbau.mit.ast.FunAst
import java.util.*

/**
 * A class for the context in which the interpretation is computed. Represented as a stack of
 * [FunScope]s.
 */
class FunContext(
    private val builtInFunctionIdentifiers: Set<FunAst.Identifier> = setOf(),
    private val scopes: java.util.ArrayDeque<FunScope> = ArrayDeque()
) {
    fun enterScope() {
        scopes.add(FunContext.FunScope())
    }

    fun leaveScope() {
        scopes.removeLast()
    }

    fun declareFunction(function: FunAst.Function) {
        val scope = scopes.peekLast()
        val scopeFunctions = scope.functions
        if (scopeFunctions.contains(function.identifier)) {
            throw FunInterpretationException(
                function.identifier.name + " function is redefined in the same scope.")
        }
        scopeFunctions.put(function.identifier, function)
    }

    /** Returns resolved function or null if the function is built-in. */
    fun getFunction(identifier: FunAst.Identifier, paramsLength: Int): FunAst.Function? {
        val scopeIterator = scopes.descendingIterator()
        while (scopeIterator.hasNext()) {
            val scope = scopeIterator.next()
            if (scope.functions.containsKey(identifier)) {
                val function = scope.functions.getValue(identifier)
                if (function.paramNames.params.size == paramsLength) {
                    return function
                }
            }
        }
        if (builtInFunctionIdentifiers.contains(identifier)) {
            return null
        }
        throw FunInterpretationException(
            identifier.name + " function with " + paramsLength +
                (if (paramsLength == 1) " param" else " params") + " is not defined.")
    }

    fun declareVariable(variableIdentifier: FunAst.Identifier, value: Int?) {
        val scope = scopes.peekLast()
        val scopeVariables = scope.variables
        if (scopeVariables.contains(variableIdentifier)) {
            throw FunInterpretationException(
                variableIdentifier.name + " variable is redefined in the same scope.")
        }
        scopeVariables.put(variableIdentifier, value)
    }

    fun getVariable(identifier: FunAst.Identifier): Pair<Int?, FunScope> {
        val scopeIterator = scopes.descendingIterator()
        while (scopeIterator.hasNext()) {
            val scope = scopeIterator.next()
            if (scope.variables.containsKey(identifier)) {
                return Pair(scope.variables.getValue(identifier), scope)
            }
        }
        throw FunInterpretationException(
            identifier.name + " variable is not defined.")
    }

    fun setVariable(scope: FunScope, identifier: FunAst.Identifier, value: Int) {
        scope.variables.put(identifier, value)
    }

    data class FunScope(
        val variables: MutableMap<FunAst.Identifier, Int?> = mutableMapOf(),
        val functions: MutableMap<FunAst.Identifier, FunAst.Function> = mutableMapOf()
    )
}