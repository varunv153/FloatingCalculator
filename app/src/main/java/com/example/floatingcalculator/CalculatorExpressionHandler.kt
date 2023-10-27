package com.example.floatingcalculator

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat
import javax.inject.Inject

class CalculatorExpressionHandler @Inject constructor() {
    fun handleOpeningBracket(currentText: String): String {
        return if ( currentText.last().isDigit() || currentText.last() == ')') {
            "$currentText*("
        } else {
            "$currentText("
        }
    }


    fun handleClosingBracket(currentText: String): String {
        val lastChar:Char = currentText.last()
        val openBracketCount = currentText.count { it == '(' }
        val closeBracketCount = currentText.count { it == ')' }
        if (openBracketCount > closeBracketCount && !isOperator(lastChar)) {
            return "$currentText)"
        }
        return currentText
    }

    fun handleEqualsButton(currentText: String): String {
        return try {
            evaluateExpression(currentText)
        } catch (e: Exception) {
            Log.e("Varun Floating Calculator", "Error while evaluating expression", e)
            currentText
        }
    }

    fun handleDeleteButton(currentText: String) : String {
        return currentText.dropLast(1)
    }

    fun overWriteCondition(currentText: String, buttonText: Char) : Boolean {
        return currentText in listOf("0", Double.NaN.toString()) && (buttonText=='(' || buttonText.isDigit())
    }
    fun handleOperators(currentText: String, buttonText: Char): String {
        var resultText:String = currentText
        val lastChar:Char? = currentText.lastOrNull()
        if (isOperator(lastChar)) {
            resultText = currentText.dropLast(1)
        }
        return resultText + buttonText
    }
    fun handleDigits(currentText: String, buttonText: Char): String {
        return currentText + buttonText
    }


    fun isOperator(char: Char?): Boolean {
        return char != null && char in "+-*/"
    }


    fun evaluateExpression(expression: String): String {
        return try {
            val result = ExpressionBuilder(expression).build().evaluate()
            val formattedResult = when {
                result.isNaN() -> "NaN"
                result.isInfinite() -> "Infinity"
                result % 1 == 0.0 -> result.toInt().toString()
                else -> formatNumber(result)
            }
            formattedResult

        } catch (e: ArithmeticException) {
            Double.NaN.toString()
        }
    }

    fun formatNumber(number: Double): String {
        val formatter = DecimalFormat("0.##############") // Adjust the number of # symbols as needed
        return formatter.format(number)
    }
}