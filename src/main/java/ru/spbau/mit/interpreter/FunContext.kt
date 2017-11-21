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

    /** Returns resolved function or null if the function is built-in. */
    fun getFunction(identifier: FunAst.Identifier, paramsLength: Int): FunAst.Function? {
        val function = findInScopes { scope ->
            if (scope.functions.containsKey(identifier)) {
                val function = scope.functions.getValue(identifier)
                if (function.paramNames.params.size == paramsLength) {
                    function
                } else {
                    null
                }
            } else {
                null
            }
        }
        function?.let { return it }
        if (builtInFunctionIdentifiers.contains(identifier)) {
            return null
        }
        throw FunInterpretationException(
            identifier.name + " function with " + paramsLength +
                (if (paramsLength == 1) " param" else " params") + " is not defined.")
    }

    fun getVariable(identifier: FunAst.Identifier): Pair<Int?, FunScope> {
        val variable = findInScopes { scope ->
            if (scope.variables.containsKey(identifier)) {
                Pair(scope.variables.getValue(identifier), scope)
            } else {
                null
            }
        }
        variable?.let { return it }
        throw FunInterpretationException(identifier.name + " variable is not defined.")
    }

    private fun <T> findInScopes(performSearch: (FunContext.FunScope) -> T?): T? {
        val scopeIterator = scopes.descendingIterator()
        for (scope in scopeIterator) {
            val finding = performSearch(scope)
            finding?.let { return it }
        }
        return null
    }

    fun declareFunction(function: FunAst.Function) {
        val scope = scopes.peekLast()
        putIfNotDefined("Function", scope.functions, function.identifier, function)
    }

    fun declareVariable(variableIdentifier: FunAst.Identifier, value: Int?) {
        val scope = scopes.peekLast()
        putIfNotDefined("Variable", scope.variables, variableIdentifier, value)
    }

    private fun <T> putIfNotDefined(
        entityName: String,
        definitions: MutableMap<FunAst.Identifier, T>,
        identifier: FunAst.Identifier,
        value: T
    ) {
        if (definitions.contains(identifier)) {
            throw FunInterpretationException(
                "$entityName \"${identifier.name}\" is redefined in the same scope.")
        }
        definitions.put(identifier, value)
    }

    fun setVariable(scope: FunScope, identifier: FunAst.Identifier, value: Int) {
        scope.variables.put(identifier, value)
    }

    data class FunScope(
        val variables: MutableMap<FunAst.Identifier, Int?> = mutableMapOf(),
        val functions: MutableMap<FunAst.Identifier, FunAst.Function> = mutableMapOf()
    )
}