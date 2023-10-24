package com.example.floatingcalculator

import android.view.View
import android.widget.Button
import android.widget.EditText
import net.objecthunter.exp4j.ExpressionBuilder

class CalculatorViewManager {

    fun handleClickInCalculator(floatingView: View, button: Button) {
        val editText: EditText = floatingView.findViewById(R.id.editText)
        val currentText: String = editText.text.toString()

        if (currentText == "0" || currentText == Double.NaN.toString()) {
            if (button.text == "=") {
            } else if (button.text == "del") {
            } else {
                editText.setText(button.text.toString())
            }
        } else {
            val lastChar = currentText.lastOrNull()

            if (button.text == "=") {
                try {
                    val result = evaluateExpression(currentText)
                    editText.setText(result.toString())
                } catch (e: Exception) {
                    editText.setText("Error")
                }
            } else if (button.text == "del") {
                if (currentText.isNotEmpty()) {
                    editText.text.delete(currentText.length - 1, currentText.length)
                }
            } else if (isOperator(lastChar) && isOperator(button.text.toString()[0])) {
                editText.text.delete(currentText.length - 1, currentText.length)
            } else {
                editText.append(button.text.toString())
            }
        }
    }

    private fun isOperator(char: Char?): Boolean {
        return char != null && char in "+-*/"
    }

    private fun evaluateExpression(expression: String): Double {
        return try {
            ExpressionBuilder(expression).build().evaluate()
        } catch (e: Exception) {
            Double.NaN
        }
    }
}
