package com.raiz.scanmecalculator

object Calculator {

    fun add(firstOperand : String , secondOperand : String): Int {
        return firstOperand.toInt() + secondOperand.toInt()
    }

    fun substract(firstOperand : String , secondOperand : String) : Int {
        return firstOperand.toInt() - secondOperand.toInt()
    }

    fun multiply(firstOperand : String , secondOperand : String) : Int {
        return firstOperand.toInt() * secondOperand.toInt()
    }

    fun divide(firstOperand : String , secondOperand : String) : Int {
        return firstOperand.toInt() / secondOperand.toInt()
    }
}
