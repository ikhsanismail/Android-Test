package com.raiz.scanmecalculator

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.lang.IllegalArgumentException

@RunWith(JUnit4::class)
class CalculatorTest {

    @Test
    fun testAddSuccess() {
        val firstOperand = "10"
        val secondOperand = "10"
        val result = Calculator.add(firstOperand,secondOperand)
        assertEquals(10,result)
    }

    @Test
    fun testSubstractSuccess() {
        val firstOperand = "10"
        val secondOperand = "10"
        val result = Calculator.substract(firstOperand,secondOperand)
        assertEquals(0,result)
    }

    @Test
    fun testMultiplySuccess() {
        val firstOperand = "10"
        val secondOperand = "10"
        val result = Calculator.multiply(firstOperand,secondOperand)
        assertEquals(100,result)
    }

    @Test
    fun testDivideSuccess() {
        val firstOperand = "10"
        val secondOperand = "10"
        val result = Calculator.divide(firstOperand,secondOperand)
        assertEquals(1,result)
    }

    @Test(expected = NumberFormatException::class)
    fun testAddError() {
        val firstOperand = " 20"
        val secondOperand = "10"
        val result = Calculator.add(firstOperand,secondOperand)
    }

    @Test(expected = NumberFormatException::class)
    fun testAddError2() {
        val firstOperand = " "
        val secondOperand = "10"
        val result = Calculator.add(firstOperand,secondOperand)
    }

    @Test(expected = NumberFormatException::class)
    fun testSubstractError() {
        val firstOperand = "10"
        val secondOperand = " "
        val result = Calculator.substract(firstOperand,secondOperand)
    }

    @Test(expected = NumberFormatException::class)
    fun testMultiplyError() {
        val firstOperand = "10"
        val secondOperand = "U"
        val result = Calculator.multiply(firstOperand,secondOperand)
    }

    @Test(expected = ArithmeticException::class)
    fun testDivideError() {
        val firstOperand = "10"
        val secondOperand = "0"
        val result = Calculator.divide(firstOperand,secondOperand)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDivideError2() {
        val firstOperand = "10"
        val secondOperand = " "
        val result = Calculator.divide(firstOperand,secondOperand)
    }
}