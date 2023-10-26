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

        val newDisplay:String = when (button.text) {
            "=" -> handleEqualsButton(displayEditText)
            else -> handleOtherButtons(displayEditText, button)
        }
        displayEditText.setText(newDisplay)

        val calculatorButton = floatingView.findViewById<ImageButton>(R.id.delete_button)
        calculatorButton.setOnLongClickListener {
            displayEditText.setText("")
            true
        }
        calculatorButton.setOnClickListener { handleDeleteButton(displayEditText) }
    }

    private fun handleEqualsButton(displayEditText: EditText): String {
        val currentText: String = displayEditText.text.toString()
        return try {
            evaluateExpression(currentText)
        } catch (e: Exception) {
            Log.e("Varun Floating Calculator", "Error while evaluating expression", e)
            currentText
        }
    }

    private fun handleDeleteButton(editText: EditText) {
        val editableText = editText.text

        if (editableText.isNotEmpty()) {
            val length = editableText.length
            editableText.delete(length - 1, length)
        }
    }

    private fun handleOtherButtons(displayEditText: EditText, button: Button): String {
        val currentText: String = displayEditText.text.toString()
        var newText = currentText
        val lastChar = currentText.lastOrNull()
        if (currentText == "0" || currentText == "NaN" || currentText.isEmpty()) {
            if (button.text != "=" && button.text != "del") {
                return button.text.toString()
            }
            return currentText
        } else {
            if (button.text == "(") {
                if (lastChar == null || lastChar.isDigit() || lastChar == ')') {
                    newText = currentText + "*"
                }
                return newText + button.text.toString()
            } else if (button.text == ")") {
                val openBracketCount = currentText.count { it == '(' }
                val closeBracketCount = currentText.count { it == ')' }
                if (openBracketCount > closeBracketCount && (lastChar == null || !isOperator(lastChar))) {
                    return currentText + button.text.toString()
                }
                return currentText
            } else {
                if (isOperator(lastChar) && isOperator(button.text[0])) {
                    currentText.dropLast(1)
                }
                return currentText + button.text.toString()
            }
        }
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
