package com.yourname.commoninfo.domain

import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorEngine {

    private var first: BigDecimal? = null
    private var op: Operator? = null
    private var input: String = "0"
    private var resetInputOnNextDigit: Boolean = false
    private var error: String? = null

    fun getDisplay(): String = error ?: input

    fun onDigit(d: Char) {
        if (error != null) return
        if (resetInputOnNextDigit) {
            input = "0"
            resetInputOnNextDigit = false
        }
        if (input == "0") input = d.toString() else input += d
    }

    fun onDot() {
        if (error != null) return
        if (resetInputOnNextDigit) {
            input = "0"
            resetInputOnNextDigit = false
        }
        if (!input.contains('.')) input += "."
    }

    fun onClear() {
        first = null
        op = null
        input = "0"
        resetInputOnNextDigit = false
        error = null
    }

    fun onBackspace() {
        if (error != null) return
        if (resetInputOnNextDigit) return
        input = if (input.length <= 1) "0" else input.dropLast(1)
        if (input == "-" || input == "-0") input = "0"
    }

    fun onToggleSign() {
        if (error != null) return
        if (input == "0") return
        input = if (input.startsWith("-")) input.drop(1) else "-$input"
    }

    fun onOperator(newOp: Operator) {
        if (error != null) return
        val current = input.toBigDecimalOrNull() ?: run {
            error = "Invalid input"
            return
        }

        if (first == null) {
            first = current
            op = newOp
            resetInputOnNextDigit = true
            return
        }

        if (resetInputOnNextDigit) {
            op = newOp
            return
        }

        val result = calculate(first!!, op!!, current) ?: return
        first = result
        input = format(result)
        op = newOp
        resetInputOnNextDigit = true
    }

    fun onEquals() {
        if (error != null) return
        val current = input.toBigDecimalOrNull() ?: run {
            error = "Invalid input"
            return
        }
        val f = first ?: return
        val operation = op ?: return
        if (resetInputOnNextDigit) return

        val result = calculate(f, operation, current) ?: return
        input = format(result)
        first = null
        op = null
        resetInputOnNextDigit = true
    }

    private fun calculate(a: BigDecimal, o: Operator, b: BigDecimal): BigDecimal? =
        try {
            when (o) {
                Operator.ADD -> a + b
                Operator.SUB -> a - b
                Operator.MUL -> a * b
                Operator.DIV -> {
                    if (b.compareTo(BigDecimal.ZERO) == 0) {
                        error = "Division by zero"
                        null
                    } else a.divide(b, 10, RoundingMode.HALF_UP)
                }
            }
        } catch (_: Throwable) {
            error = "Error"
            null
        }

    private fun format(v: BigDecimal): String =
        v.stripTrailingZeros().toPlainString()

    enum class Operator { ADD, SUB, MUL, DIV }
}
