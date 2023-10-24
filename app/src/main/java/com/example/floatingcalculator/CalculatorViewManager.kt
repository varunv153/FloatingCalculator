package com.example.floatingcalculator

import android.util.Log
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
                    Log.e("Varun Floating Calculator", "Error while evaluating expression", e )
                }
            } else if (button.text == "del") {
                if (currentText.isNotEmpty()) {
                    editText.text.delete(currentText.length - 1, currentText.length)
                }
            } else {
                if (isOperator(lastChar) && isOperator(button.text.toString()[0])) {
                    editText.text.delete(currentText.length - 1, currentText.length)
                }
                editText.append(button.text.toString())
            }
        }

        val calculatorButton = floatingView.findViewById<Button>(R.id.delete_button)
        calculatorButton.setOnLongClickListener { v ->
            editText.setText("")
            true
        }

    }

    private fun isOperator(char: Char?): Boolean {
        return char != null && char in "+-*/"
    }

    private fun evaluateExpression(expression: String): Double {
        return try {
            ExpressionBuilder(expression).build().evaluate()
        } catch (e: ArithmeticException) {
            Double.NaN
        }
    }
}
