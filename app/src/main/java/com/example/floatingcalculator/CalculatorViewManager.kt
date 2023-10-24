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
        val editText: EditText = floatingView.findViewById(R.id.editText)
        val currentText: String = editText.text.toString()
        val lastChar = currentText.lastOrNull()

        when (button.text) {
            "=" -> handleEqualsButton(editText, currentText)
            else -> handleOtherButtons(editText, currentText, lastChar, button)
        }

        val calculatorButton = floatingView.findViewById<ImageButton>(R.id.delete_button)
        calculatorButton.setOnLongClickListener {
            editText.setText("")
            true
        }
        calculatorButton.setOnClickListener { handleDeleteButton(editText) }
    }

    private fun handleEqualsButton(editText: EditText, currentText: String) {
        try {
            editText.setText(evaluateExpression(currentText))

        } catch (e: Exception) {
            Log.e("Varun Floating Calculator", "Error while evaluating expression", e)
        }
    }

    private fun handleDeleteButton(editText: EditText) {
        val text = editText.text
        val cursorPosition = editText.selectionStart

        if (cursorPosition > 0) {
            text.delete(cursorPosition - 1, cursorPosition)
        }
    }

    private fun handleOtherButtons(editText: EditText, currentText: String, lastChar: Char?, button: Button) {
        if (currentText == "0" || currentText == "NaN") {
            if (button.text != "=" && button.text != "del") {
                editText.setText(button.text.toString())
            }
        } else {
            if (button.text == "(") {
                // Check if the last character is a digit or a closing bracket
                if (lastChar == null || lastChar.isDigit() || lastChar == ')') {
                    editText.append("*") // Automatically insert a multiplication operator
                }
                editText.append(button.text.toString())
            } else if (button.text == ")") {
                // Check if there's a matching open bracket
                val openBracketCount = currentText.count { it == '(' }
                val closeBracketCount = currentText.count { it == ')' }
                if (openBracketCount > closeBracketCount) {
                    editText.append(button.text.toString())
                }
            } else if (isOperator(lastChar) && isOperator(button.text[0])) {
                editText.text.delete(currentText.length - 1, currentText.length)
            } else {
                editText.append(button.text.toString())
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
