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
        val lastChar = currentText.lastOrNull()

        when (button.text) {
            "=" -> handleEqualsButton(editText, currentText)
            "del" -> handleDeleteButton(editText, currentText)
            else -> handleOtherButtons(editText, currentText, lastChar, button)
        }

        val calculatorButton = floatingView.findViewById<Button>(R.id.delete_button)
        calculatorButton.setOnLongClickListener {
            editText.setText("")
            true
        }
    }

    private fun handleEqualsButton(editText: EditText, currentText: String) {
        try {
            val result: Double = evaluateExpression(currentText)
            editText.setText(result.toString())

        } catch (e: Exception) {
            Log.e("Varun Floating Calculator", "Error while evaluating expression", e)
        }
    }

    private fun handleDeleteButton(editText: EditText, currentText: String) {
        if (currentText.isNotEmpty()) {
            editText.text.delete(currentText.length - 1, currentText.length)
        }
    }

    private fun handleOtherButtons(editText: EditText, currentText: String, lastChar: Char?, button: Button) {
        if (currentText == "0" || currentText == "NaN") {
            if (button.text != "=" && button.text != "del") {
                editText.setText(button.text.toString())
            }
        } else {
            if ( isOperator(lastChar) && isOperator(button.text[0])) {
                editText.text.delete(currentText.length - 1, currentText.length)
            }
            editText.append(button.text.toString())
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
