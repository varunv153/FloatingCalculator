package com.example.floatingcalculator

import android.view.View
import android.widget.Button
import android.widget.EditText

class CalculatorViewManager {

    fun handleClickInCalculator (floatingView: View, button: Button ) {
        val editText: EditText = floatingView.findViewById(R.id.editText)
        val currentText:String = editText.text.toString()
        if (currentText == "0" || currentText == "Error") {
            editText.setText(button.text.toString())
        } else {
            editText.append(button.text.toString())
        }
    }
}