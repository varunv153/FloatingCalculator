package com.example.floatingcalculator

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import javax.inject.Inject


class CalculatorViewManager @Inject constructor(private val calculatorExpressionHandler: CalculatorExpressionHandler) {


    fun handleClickInCalculator(floatingView: View, button: Button) {
        val displayEditText: EditText = floatingView.findViewById(R.id.calculator_display)
        val currentString:String  = displayEditText.text.toString()
        val buttonText:Char = button.text.toString()[0]

        if (calculatorExpressionHandler.overWriteCondition(currentString, buttonText)) {
            displayEditText.setText(buttonText.toString())
        } else {
            val newDisplay: String = when (buttonText) {
                '=' -> calculatorExpressionHandler.handleEqualsButton(currentString)
                '(' -> calculatorExpressionHandler.handleOpeningBracket(currentString)
                ')' -> calculatorExpressionHandler.handleClosingBracket(currentString)
                '.' -> calculatorExpressionHandler.handleDecimalPoint(currentString)
                in '0'..'9' -> calculatorExpressionHandler.handleDigits(currentString, buttonText)
                else -> calculatorExpressionHandler.handleOperators(currentString, buttonText)
            }
            displayEditText.setText(newDisplay)
        }

        val calculatorButton = floatingView.findViewById<ImageButton>(R.id.delete_button)
        calculatorButton.setOnLongClickListener {
            displayEditText.setText("0")
            true
        }
        calculatorButton.setOnClickListener {
            displayEditText.setText(calculatorExpressionHandler.handleDeleteButton(displayEditText.text.toString()))
        }
    }
}
