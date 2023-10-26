package com.example.floatingcalculator

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat

class CalculatorViewManager {

    fun handleClickInCalculator(floatingView: View, button: Button) {
        val displayEditText: EditText = floatingView.findViewById(R.id.calculator_display)
        val currentString:String  = displayEditText.text.toString()
        val buttonText:Char = button.text.toString()[0]

        if (overWriteCondition(currentString, buttonText)) {
            displayEditText.setText(buttonText.toString())
        } else {
            val newDisplay: String = when (buttonText) {
                '=' -> handleEqualsButton(currentString)
                '(' -> handleOpeningBracket(currentString)
                ')' -> handleClosingBracket(currentString)
                else -> {
                    if (buttonText.isDigit()) {
                        handleDigits(currentString, buttonText)
                    } else {
                        handleOperators(currentString, buttonText)
                    }
                }
            }
            displayEditText.setText(newDisplay)
        }

        val calculatorButton = floatingView.findViewById<ImageButton>(R.id.delete_button)
        calculatorButton.setOnLongClickListener {
            displayEditText.setText("0")
            true
        }
        calculatorButton.setOnClickListener {
            displayEditText.setText(handleDeleteButton(displayEditText.text.toString()))
        }
    }

    private fun handleOpeningBracket(currentText: String): String {
        return if ( currentText.last().isDigit() || currentText.last() == ')') {
            "$currentText*("
        } else {
            "$currentText("
        }
    }


    private fun handleClosingBracket(currentText: String): String {
        val lastChar:Char = currentText.last()
        val openBracketCount = currentText.count { it == '(' }
        val closeBracketCount = currentText.count { it == ')' }
        if (openBracketCount > closeBracketCount && !isOperator(lastChar)) {
            return currentText + ")"
        }
        return currentText
    }

    private fun handleEqualsButton(currentText: String): String {
        return try {
            evaluateExpression(currentText)
        } catch (e: Exception) {
            Log.e("Varun Floating Calculator", "Error while evaluating expression", e)
            currentText
        }
    }

    private fun handleDeleteButton(currentText: String) : String {
        return currentText.dropLast(1)
    }

    private fun overWriteCondition(currentText: String, buttonText: Char) : Boolean {
        return currentText in listOf("0", Double.NaN.toString()) && (buttonText=='(' || buttonText.isDigit())
    }
    private fun handleOperators(currentText: String, buttonText: Char): String {
        var resultText:String = currentText
        val lastChar:Char? = currentText.lastOrNull()
        if (isOperator(lastChar)) {
            resultText = currentText.dropLast(1)
        }
        return resultText + buttonText
    }
    private fun handleDigits(currentText: String, buttonText: Char): String {
        return currentText + buttonText
    }


    private fun isOperator(char: Char?): Boolean {
        return char != null && char in "+-*/"
    }


    private fun evaluateExpression(expression: String): String {
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
