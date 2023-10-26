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
            "=" -> handleEqualsButton(displayEditText.text.toString())
            "(" -> handleOpeningBracket(displayEditText.text.toString())
            ")" -> handleClosingBracket(displayEditText.text.toString())
            else -> handleOtherButtons(displayEditText.text.toString(), button.text.toString())
        }
        displayEditText.setText(newDisplay)

        val calculatorButton = floatingView.findViewById<ImageButton>(R.id.delete_button)
        calculatorButton.setOnLongClickListener {
            displayEditText.setText("")
            true
        }
        calculatorButton.setOnClickListener { handleDeleteButton(displayEditText) }
    }

    private fun handleOpeningBracket(currentText: String): String {
        if (overWriteCondition(currentText)) {
            return "("
        }
        var resultText:String = currentText
        val lastChar:Char = currentText.lastOrNull() ?: ' '
        if (lastChar.isDigit() || lastChar == ')') {
            resultText = "$currentText*"
        }
        return resultText + "("
    }

    private fun handleClosingBracket(currentText: String): String {
        val lastChar:Char? = currentText.lastOrNull()
        val openBracketCount = currentText.count { it == '(' }
        val closeBracketCount = currentText.count { it == ')' }
        if (openBracketCount > closeBracketCount && (lastChar == null || !isOperator(lastChar))) {
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

    private fun handleDeleteButton(editText: EditText) {
        val editableText = editText.text

        if (editableText.isNotEmpty()) {
            val length = editableText.length
            editableText.delete(length - 1, length)
        }
    }

    private fun overWriteCondition(currentText: String) : Boolean {
        return currentText in listOf("0", Double.NaN.toString(), "")
    }
    private fun handleOtherButtons(currentText: String, buttonText: String): String {
        var resultText:String = currentText
        val lastChar:Char? = currentText.lastOrNull()
        if (overWriteCondition(currentText)) {
            return buttonText
        } else {
            if (isOperator(lastChar) && isOperator(buttonText[0])) {
                resultText = currentText.dropLast(1)
            }
            return resultText + buttonText
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
