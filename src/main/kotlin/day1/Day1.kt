package day1

import java.io.File

class Day1(inputFilePath: String, private val expectedSum: Int, private val amountOfNumbers: Int) {
    companion object {
        fun exerciseOne() {
            Day1("./src/main/kotlin/day1/realInput.txt", 2020, 3)
                .findAndPrintSummingNumbers()
        }
    }

    private val allNumbers: List<Int> = File(inputFilePath).readLines().map { it.toInt() }
    private val calculatedNumbers = HashSet<Int>()

    private fun findAndPrintSummingNumbers() {
        for (i in allNumbers.indices) {
            val topLevelNumber = allNumbers[i]

            if (calculatedNumbers.contains(topLevelNumber)) continue

            if (addNextNumberUntillResultIsReached(listOf(topLevelNumber), i + 1)) return

            calculatedNumbers.add(topLevelNumber)
        }

        println("There was no solution")
    }

    private fun addNextNumberUntillResultIsReached(numbersAddedUp: List<Int>, currentIndex: Int): Boolean {
        val newNumbersAddedUp = numbersAddedUp.toMutableList()
        newNumbersAddedUp.add(allNumbers[currentIndex])

        return if (checkSumAndContinueIfInvalid(newNumbersAddedUp, currentIndex + 1)) {
            true
        } else {
            checkSumAndContinueIfInvalid(numbersAddedUp, currentIndex + 1)
        }
    }

    private fun checkSumAndContinueIfInvalid(numbersAddedUp: List<Int>, currentIndex: Int): Boolean {
        val currentSum = numbersAddedUp.sum()
        val currentIteration = numbersAddedUp.size

        return if (currentSum == expectedSum && currentIteration == amountOfNumbers) {
            // Success
            printResult(numbersAddedUp, currentSum)
        } else if (currentSum > expectedSum || currentIndex == allNumbers.size || currentIteration == amountOfNumbers) {
            // End of line
            false
        } else {
            // Keep searching
            return addNextNumberUntillResultIsReached(numbersAddedUp, currentIndex)
        }
    }

    private fun printResult(numbersAddedUp: List<Int>, currentSum: Int): Boolean {
        val result: Long = numbersAddedUp.map { it.toLong() }.reduce { sum, element -> element * sum }
        println("Adding $numbersAddedUp together gives $currentSum - multiplying those numbers gives $result")
        return true
    }
}
