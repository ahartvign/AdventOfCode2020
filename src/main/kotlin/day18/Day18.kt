package day18

import java.io.File
import java.lang.IllegalStateException

class Day18(inputFilePath: String) {
    companion object {
        fun exerciseEighteen() {
            Day18("./src/main/kotlin/day18/realInput.txt").part1()
        }
    }

    private val allLines: List<String> = File(inputFilePath).readLines()

    private fun part1() {
        var result: Long = 0

        for (line in allLines) {
            val lineWithoutSpaces = line.replace("\\s".toRegex(), "")
            val lineResult = AlternatePart1ExpressionCalculator(lineWithoutSpaces).calculateExpression()

            print("$line = $lineResult \n")
            result += lineResult
        }

        print("The total result was: $result")
    }

    private fun part2() {
        var result: Long = 0

        for (line in allLines) {
            val lineWithoutSpaces = line.replace("\\s".toRegex(), "")
            val lineResult = Part2ExpressionCalculator(lineWithoutSpaces).calculateExpression()

            print("$line = $lineResult \n")
            result += lineResult
        }

        print("The total result was: $result")
    }

    private abstract class AbstractExpressionCalculator {
        protected var result: Long = 0
        protected var previousNumber: Long? = null
        protected var currentNumberString = ""
        protected var currentOperator: Char? = null

        protected abstract fun createCalculator(subExpression: String): AbstractExpressionCalculator

        protected abstract fun calculateExpression(): Long

        protected fun handleExpressionBeforeOperator() {
            when {
                currentOperator != null -> {
                    consumeCurrentOperator(currentNumberString.toLong())
                }
                else -> {
                    if (currentNumberString.isNotEmpty()) {
                        previousNumber = currentNumberString.toLong()
                    }
                }
            }

            currentNumberString = ""
        }

        protected fun handleSubExpression(subExpression: String) {
            val expressionResult = createCalculator(subExpression).calculateExpression()

            if (currentOperator != null) {
                consumeCurrentOperator(expressionResult)
            } else {
                result += expressionResult
            }
        }

        protected fun consumeCurrentOperator(valueAddedOn: Long) {
            when {
                previousNumber != null -> {
                    result += useOperator(currentOperator!!, previousNumber!!, valueAddedOn)
                    previousNumber = null
                }
                else -> {
                    result = useOperator(currentOperator!!, result, valueAddedOn)
                }
            }

            currentOperator = null
        }

        protected fun findClosingBracketIndex(line: String, startBracketIndex: Int): Int {
            var level = 0
            val subLine = line.substring(startBracketIndex + 1)

            for ((index, char) in subLine.withIndex()) {
                if (char == '(') {
                    level++
                } else if (char == ')') {
                    if (level == 0) {
                        return index + startBracketIndex + 1
                    } else {
                        level--
                    }
                }
            }

            throw IllegalStateException("Found no end to bracket on line: '$subLine'")
        }

        private fun useOperator(operator: Char, result: Long, value: Long): Long {
            return when (operator) {
                '*' -> result * value
                '/' -> result / value
                '+' -> result + value
                '-' -> result - value
                else -> {
                    throw IllegalStateException("Unknown operator: $operator")
                }
            }
        }

        protected fun isOperator(lineChar: Char): Boolean {
            return lineChar == '*' || lineChar == '/' || lineChar == '+' || lineChar == '-'
        }
    }

    private class Part1ExpressionCalculator(private val line: String): AbstractExpressionCalculator() {
        override fun createCalculator(subExpression: String): AbstractExpressionCalculator {
            return Part1ExpressionCalculator(subExpression)
        }

        public override fun calculateExpression(): Long {
            var index = 0

            while (index < line.length) {
                val lineChar: Char = line[index]

                when {
                    isOperator(lineChar) -> {
                        handleExpressionBeforeOperator()
                        currentOperator = lineChar
                        index++
                    }
                    lineChar == '(' -> {
                        val closingBracket = findClosingBracketIndex(line, index)
                        val subExpression = line.subSequence(index + 1, closingBracket).toString()

                        handleSubExpression(subExpression)

                        index = closingBracket + 1
                    }
                    else -> {
                        currentNumberString += lineChar
                        index++
                    }
                }
            }

            if (currentOperator != null) {
                consumeCurrentOperator(currentNumberString.toLong())
            }

            return result
        }
    }

    private class AlternatePart1ExpressionCalculator(private val line: String): AbstractExpressionCalculator() {
        override fun createCalculator(subExpression: String): AbstractExpressionCalculator {
            return AlternatePart1ExpressionCalculator(subExpression)
        }

        public override fun calculateExpression(): Long {
            return calculateExpressionRecursively(line);
        }

        private fun calculateExpressionRecursively(subLine: String): Long {
            var replacedLine = subLine
            val startIndexOfSubExpression = subLine.indexOf('(', 0)

            if (startIndexOfSubExpression == -1) {
                return AlternatePart1ExpressionCalculator(replacedLine).doSimpleCalculation()
            }

            val endIndexOfSubExpression = findClosingBracketIndex(subLine, startIndexOfSubExpression)
            val subExpression = subLine.subSequence(startIndexOfSubExpression + 1, endIndexOfSubExpression).toString()
            val valueOfSubExpression = calculateExpressionRecursively(subExpression)

            replacedLine = subLine.replaceRange(
                startIndexOfSubExpression,
                endIndexOfSubExpression + 1,
                valueOfSubExpression.toString())
            return calculateExpressionRecursively(replacedLine)
        }

        private fun doSimpleCalculation(): Long {
            var index = 0

            while (index < line.length) {
                val lineChar: Char = line[index]

                when {
                    isOperator(lineChar) -> {
                        handleExpressionBeforeOperator()
                        currentOperator = lineChar
                        index++
                    }
                    else -> {
                        currentNumberString += lineChar
                        index++
                    }
                }
            }

            if (currentOperator != null) {
                consumeCurrentOperator(currentNumberString.toLong())
            }

            return result
        }
    }

    private class Part2ExpressionCalculator(private val line: String): AbstractExpressionCalculator() {
        override fun createCalculator(subExpression: String): AbstractExpressionCalculator {
            return Part2ExpressionCalculator(subExpression)
        }

        public override fun calculateExpression(): Long {
            var index = 0

            while (index < line.length) {
                val lineChar: Char = line[index]

                if (isOperator(lineChar)) {
                    handleExpressionBeforeOperator()
                    currentOperator = lineChar

                    if (currentOperator == '*') {
                        index = handleMultiplicationAsSubExpression(index)
                    } else {
                        index++
                    }
                }
                else if (lineChar == '(') {
                    val closingBracket = findClosingBracketIndex(line, index)
                    val subExpression = line.subSequence(index + 1, closingBracket).toString()

                    handleSubExpression(subExpression)

                    index = closingBracket + 1
                }
                else {
                    currentNumberString += lineChar
                    index++
                }
            }

            if (currentOperator != null) {
                consumeCurrentOperator(currentNumberString.toLong())
            }

            return result
        }

        private fun handleMultiplicationAsSubExpression(index: Int): Int {
            val nextMultiplication = findEndOfMultiplication(line, index)
            val subExpression = line.subSequence(index + 1, nextMultiplication).toString()

            val subExpressionAsLongOrNull = subExpression.toLongOrNull()

            if (subExpressionAsLongOrNull == null) {
                handleSubExpression(subExpression)
            } else {
                consumeCurrentOperator(subExpressionAsLongOrNull)
            }

            return nextMultiplication
        }

        private fun findEndOfMultiplication(line: String, startMultiplicationIndex: Int): Int {
            var level = 0
            val subLine = line.substring(startMultiplicationIndex + 1)

            for ((index, char) in subLine.withIndex()) {
                if (char == '(') {
                    level++
                } else if (char == ')') {
                    level--
                } else if (level == 0 && char == '*') {
                    return index + startMultiplicationIndex + 1
                }
            }

            return line.length
        }
    }
}